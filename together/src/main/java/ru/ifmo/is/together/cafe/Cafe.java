package ru.ifmo.is.together.cafe;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.persistence.*;
import ru.ifmo.is.together.common.framework.CrudEntity;
import ru.ifmo.is.together.users.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "cafes")
public class Cafe extends CrudEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cafes_id_seq")
  @SequenceGenerator(name = "cafes_id_seq", sequenceName = "cafes_id_seq", allocationSize = 1)
  @Column(name="id", nullable=false, unique=true)
  private int id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Size(max = 255)
  @Column(name = "poster")
  private String poster;

  @Column(name = "description")
  private String description;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "working_hours", length = 100)
  private String workingHours;

  @Column(name = "alcohol_permission")
  private Boolean alcoholPermission;

  @Column(name = "smoking_permission")
  private Boolean smokingPermission;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User owner;
}

