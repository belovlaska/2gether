package ru.ifmo.is.together.userroles;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.ifmo.is.together.common.entity.BaseEntity;
import ru.ifmo.is.together.users.User;

import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_roles")
public class UserRole implements BaseEntity {
  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_roles_id_seq")
  @SequenceGenerator(name = "user_roles_id_seq", sequenceName = "user_roles_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false, unique = true)
  private int id;

  @Enumerated(EnumType.STRING)
  @ColumnTransformer(write = "?::roles")
  @Column(name = "role", nullable = false)
  private Role role;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    UserRole userRole = (UserRole) object;
    return role == userRole.role && Objects.equals(user, userRole.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, user);
  }
}
