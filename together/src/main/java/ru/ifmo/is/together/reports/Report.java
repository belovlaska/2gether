package ru.ifmo.is.together.reports;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.framework.CrudEntity;
import ru.ifmo.is.together.users.User;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
public class Report extends CrudEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reports_id_seq")
  @SequenceGenerator(name = "reports_id_seq", sequenceName = "reports_id_seq", allocationSize = 1)
  @Column(name="id", nullable=false, unique=true)
  private int id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sender_id")
  private User sender;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "cafe_id")
  private Cafe cafe;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  @NotNull
  @Column(name = "resolved", nullable = false)
  private boolean resolved;

  @Column(name = "resolved_at")
  private Instant resolvedAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "resolver_id")
  private User resolvedBy;

  @NotNull
  @Length(min = 1, max = 127)
  @Column(name = "issue", nullable = false)
  private String issue;

  @Column(name = "text")
  private String text;

  @NotNull
  @Column(name = "date", nullable = false)
  private Instant date;
}
