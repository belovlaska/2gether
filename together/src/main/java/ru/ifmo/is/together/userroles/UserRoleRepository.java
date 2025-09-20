package ru.ifmo.is.together.userroles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
  Set<UserRole> findByUserId(Integer id);
}
