package ru.ifmo.is.together.drink;

import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "drinks")
public class Drink extends CrudEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drinks_id_seq")
  @SequenceGenerator(name = "drinks_id_seq", sequenceName = "drinks_id_seq", allocationSize = 1)
  private int id;

  @Column(name = "name", length = 20)
  private String name;

  @Column(name = "ingredients", length = 200)
  private String ingredients;

  @Column(name = "cost")
  private Integer cost;

  @Column(name = "is_alcoholic")
  private Boolean isAlcoholic;

  @ManyToOne
  @JoinColumn(name = "cafe_id")
  private Cafe cafe;

}

