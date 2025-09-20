package ru.ifmo.is.together.users;

import jakarta.validation.constraints.Email;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ru.ifmo.is.together.common.framework.CrudEntity;
import ru.ifmo.is.together.userroles.Role;
import ru.ifmo.is.together.userroles.UserRole;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends CrudEntity implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
  @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
  @Column(name="id", nullable=false, unique=true)
  private int id;

  @NotBlank
  @Length(min = 3, max = 63)
  @Column(name="username", nullable=false, unique=true)
  private String username;

  @NotBlank
  @Email(
    message = "Email is not valid",
    regexp = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?"
  )
  @Length(min = 6, max = 127)
  @Column(name="email", nullable=false, unique=true)
  private String email;

  @JsonIgnore
  @ToString.Exclude
  @Column(name="password", nullable=false, unique=true)
  private String password;

  @Column(name="bio")
  private String bio;

  @Column(name="photo")
  private String photo;

  @JsonManagedReference
  @BatchSize(size = 50)
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
  private Set<UserRole> roles;

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
      .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
      .collect(Collectors.toSet());
  }

  @JsonIgnore
  public boolean isAdmin() {
    return this.roles.stream()
      .map(UserRole::getRole).toList()
      .contains(Role.ROLE_ADMIN);
  }

  @JsonIgnore
  public boolean isUser() {
    return this.roles.stream()
      .map(UserRole::getRole).toList()
      .contains(Role.ROLE_USER);
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return true;
  }
}
