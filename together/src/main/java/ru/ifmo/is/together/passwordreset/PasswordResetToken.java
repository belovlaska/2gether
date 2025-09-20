package ru.ifmo.is.together.passwordreset;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import ru.ifmo.is.together.common.entity.BaseEntity;
import ru.ifmo.is.together.users.User;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_changes")
public class PasswordResetToken implements BaseEntity {
  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_changes_id_seq")
  @SequenceGenerator(name = "password_changes_id_seq", sequenceName = "password_changes_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false, unique = true)
  private int id;

  @Column(name = "token", nullable = false, unique = true)
  private String token;

  @Column(name = "date", nullable = false)
  private Instant date;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
