package ru.ifmo.is.together.userroles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.ifmo.is.together.common.errors.PolicyViolationError;
import ru.ifmo.is.together.common.errors.ResourceAlreadyExists;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.userroles.dto.UserRoleChangeDto;
import ru.ifmo.is.together.users.User;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository repository;

    @Mock
    private UserRolePolicy policy;

    @InjectMocks
    private UserRoleService userRoleService;

    private User testUser;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .username("testuser")
                .build();
        testUser.setRoles(new HashSet<>());

        adminUser = User.builder()
                .id(2)
                .username("admin")
                .build();
        adminUser.setRoles(new HashSet<>());
        adminUser.getRoles().add(UserRole.builder().role(Role.ROLE_ADMIN).build());

        regularUser = User.builder()
                .id(3)
                .username("regular")
                .build();
        regularUser.setRoles(new HashSet<>());
    }

    @Test
    @DisplayName("Should add role to user successfully")
    void shouldAddRoleToUserSuccessfully() {
        // Given
        UserRoleChangeDto changeDto = new UserRoleChangeDto();
        changeDto.setRole(Role.ROLE_USER);
        
        when(adminUser.isAdmin()).thenReturn(true);

        // When
        User result = userRoleService.add(testUser, adminUser, changeDto);

        // Then
        assertEquals(testUser, result);
        verify(policy, times(1)).update(adminUser, testUser);
        verify(repository, times(1)).save(any(UserRole.class));
    }

    @Test
    @DisplayName("Should throw ResourceAlreadyExists when user already has role")
    void shouldThrowResourceAlreadyExistsWhenUserAlreadyHasRole() {
        // Given
        UserRoleChangeDto changeDto = new UserRoleChangeDto();
        changeDto.setRole(Role.ROLE_USER);
        
        UserRole existingRole = UserRole.builder().role(Role.ROLE_USER).build();
        testUser.getRoles().add(existingRole);

        // When & Then
        assertThrows(ResourceAlreadyExists.class, () -> {
            userRoleService.add(testUser, adminUser, changeDto);
        });
        verify(repository, never()).save(any(UserRole.class));
    }

    @Test
    @DisplayName("Should throw PolicyViolationError when non-admin tries to add admin role")
    void shouldThrowPolicyViolationErrorWhenNonAdminTriesToAddAdminRole() {
        // Given
        UserRoleChangeDto changeDto = new UserRoleChangeDto();
        changeDto.setRole(Role.ROLE_ADMIN);
        
        when(regularUser.isAdmin()).thenReturn(false);

        // When & Then
        assertThrows(PolicyViolationError.class, () -> {
            userRoleService.add(testUser, regularUser, changeDto);
        });
        verify(repository, never()).save(any(UserRole.class));
    }

    @Test
    @DisplayName("Should remove role from user successfully")
    void shouldRemoveRoleFromUserSuccessfully() {
        // Given
        UserRoleChangeDto changeDto = new UserRoleChangeDto();
        changeDto.setRole(Role.ROLE_USER);
        
        UserRole existingRole = UserRole.builder().role(Role.ROLE_USER).build();
        testUser.getRoles().add(existingRole);
        
        when(adminUser.isAdmin()).thenReturn(true);

        // When
        User result = userRoleService.remove(testUser, adminUser, changeDto);

        // Then
        assertEquals(testUser, result);
        verify(policy, times(1)).delete(adminUser, testUser);
        verify(repository, times(1)).delete(any(UserRole.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user doesn't have role to remove")
    void shouldThrowResourceNotFoundExceptionWhenUserDoesNotHaveRoleToRemove() {
        // Given
        UserRoleChangeDto changeDto = new UserRoleChangeDto();
        changeDto.setRole(Role.ROLE_USER);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userRoleService.remove(testUser, adminUser, changeDto);
        });
        verify(repository, never()).delete(any(UserRole.class));
    }

    @Test
    @DisplayName("Should add single role successfully")
    void shouldAddSingleRoleSuccessfully() {
        // Given
        Role role = Role.ROLE_USER;

        // When
        User result = userRoleService.addRole(testUser, role);

        // Then
        assertEquals(testUser, result);
        verify(repository, times(1)).save(any(UserRole.class));
    }

    @Test
    @DisplayName("Should add multiple roles successfully")
    void shouldAddMultipleRolesSuccessfully() {
        // Given
        Set<UserRole> newRoles = new HashSet<>();
        newRoles.add(UserRole.builder().role(Role.ROLE_USER).build());
        newRoles.add(UserRole.builder().role(Role.ROLE_MODERATOR).build());

        // When
        User result = userRoleService.addRoles(testUser, newRoles);

        // Then
        assertEquals(testUser, result);
        verify(repository, times(1)).saveAll(any(Set.class));
    }

    @Test
    @DisplayName("Should delete role successfully")
    void shouldDeleteRoleSuccessfully() {
        // Given
        Role role = Role.ROLE_USER;
        UserRole existingRole = UserRole.builder().role(Role.ROLE_USER).build();
        testUser.getRoles().add(existingRole);

        // When
        User result = userRoleService.deleteRole(testUser, role);

        // Then
        assertEquals(testUser, result);
        verify(repository, times(1)).delete(any(UserRole.class));
    }
}