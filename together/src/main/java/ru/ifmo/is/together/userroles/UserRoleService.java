package ru.ifmo.is.together.userroles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.is.together.common.errors.PolicyViolationError;
import ru.ifmo.is.together.common.errors.ResourceAlreadyExists;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.userroles.dto.UserRoleChangeDto;
import ru.ifmo.is.together.users.User;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {

  private final UserRoleRepository repository;
  private final UserRolePolicy policy;

  @Transactional
  public User add(User user, User currentUser, UserRoleChangeDto dto) {
    policy.update(currentUser, user);
    var role = dto.getRole();

    if (user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()).contains(role)) {
      throw new ResourceAlreadyExists("User already has role " + role);
    }

    if (!currentUser.isAdmin() && role.equals(Role.ROLE_ADMIN)) {
      throw new PolicyViolationError("Only admins can invite admins");
    }

    return addRole(user, role);
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public User remove(User user, User currentUser, UserRoleChangeDto dto) {
    policy.delete(currentUser, user);
    var role = dto.getRole();

    if (!user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()).contains(role)) {
      throw new ResourceNotFoundException("User has no role " + role);
    }

    return deleteRole(user, role);
  }

  @Transactional
  public User addRole(User user, Role role) {
    var userRole = UserRole.builder().user(user).role(role).build();
    var roles = user.getRoles();

    if (!roles.contains(userRole) && roles.add(userRole)) {
      repository.save(userRole);
    }

    return user;
  }

  @Transactional
  public User addRoles(User user, Set<UserRole> newRoles) {
    var roles = user.getRoles();
    roles.addAll(newRoles);
    user.setRoles(roles);
    repository.saveAll(roles);
    return user;
  }

  @Transactional
  public User deleteRole(User user, Role role) {
    var userRole = UserRole.builder().user(user).role(role).build();
    var roles = user.getRoles();

    if (roles.remove(userRole)) {
      repository.delete(userRole);
    }

    return user;
  }
}
