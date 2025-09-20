package ru.ifmo.is.together.game;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.framework.CrudEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "games")
public class Game extends CrudEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "games_id_seq")
  @SequenceGenerator(name = "games_id_seq", sequenceName = "games_id_seq", allocationSize = 1)
  @Column(name="id", nullable=false, unique=true)
  private int id;

  @NotNull
  @Column(name = "name", nullable = false, length = 20)
  private String name;

  @Column(name = "genre", length = 200)
  private String genre;

  @Column(name = "age_constraint")
  private Integer age_constraint;

  @NotNull
  @Column(name = "description")
  private String description;

  @ManyToOne
  @JoinColumn(name = "cafe_id")
  private Cafe cafe;

}
