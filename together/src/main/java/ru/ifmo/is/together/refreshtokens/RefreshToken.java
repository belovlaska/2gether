package ru.ifmo.is.together.refreshtokens;

import lombok.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "refresh_tokens")
public class RefreshToken implements BaseEntity {
  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_tokens_id_seq")
  @SequenceGenerator(name = "refresh_tokens_id_seq", sequenceName = "refresh_tokens_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false, unique = true)
  private int id;

  @Column(name = "token", nullable = false, unique = true)
  private String token;

  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
