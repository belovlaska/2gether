package ru.ifmo.is.together.food;

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
@Table(name = "foods")
public class Food extends CrudEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "foods_id_seq")
  @SequenceGenerator(name = "foods_id_seq", sequenceName = "foods_id_seq", allocationSize = 1)
  @Column(name="id", nullable=false, unique=true)
  private int id;

  @Column(name = "name", nullable = false, length = 20)
  private String name;

  @Column(name = "ingredients", length = 200)
  private String ingredients;

  @Column(name = "cost")
  private Integer cost;

  @Column(name = "is_hot")
  private Boolean isHot;

  @Column(name = "is_spicy")
  private Boolean isSpicy;

  @ManyToOne
  @JoinColumn(name = "cafe_id")
  private Cafe cafe;

}

