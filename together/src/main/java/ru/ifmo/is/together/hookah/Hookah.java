package ru.ifmo.is.together.hookah;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.framework.CrudEntity;
import ru.ifmo.is.together.users.User;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "hookahs")
public class Hookah extends CrudEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hookahs_id_seq")
  @SequenceGenerator(name = "hookahs_id_seq", sequenceName = "hookahs_id_seq", allocationSize = 1)
  private int id;

  @Column(name = "tobacco")
  private String tobacco;

  @Column(name = "strength")
  private Integer strength;

  @Column(name = "cost")
  private Integer cost;

  @Column(name = "taste", length = 30)
  private String taste;

  @ManyToOne
  @JoinColumn(name = "cafe_id")
  private Cafe cafe;

}
