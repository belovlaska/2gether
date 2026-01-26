package ru.ifmo.is.together.tastes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "tastes")
public class Taste {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tastes_id_seq")
    @SequenceGenerator(name = "tastes_id_seq", sequenceName = "tastes_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

}