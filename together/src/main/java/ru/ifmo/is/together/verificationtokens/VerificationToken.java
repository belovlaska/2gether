package ru.ifmo.is.together.verificationtokens;

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
@Table(name = "verification_tokens")
public class VerificationToken implements BaseEntity {
  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_tokens_id_seq")
  @SequenceGenerator(name = "verification_tokens_id_seq", sequenceName = "verification_tokens_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false, unique = true)
  private int id;

  @Column(name = "token", nullable = false, unique = true)
  private String token;

  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @Column(name = "sent_at", nullable = false)
  private Instant sentAt;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
