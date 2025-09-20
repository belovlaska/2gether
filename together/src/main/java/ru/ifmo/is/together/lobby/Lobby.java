package ru.ifmo.is.together.lobby;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.framework.CrudEntity;
import ru.ifmo.is.together.users.User;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "lobbies")
public class Lobby extends CrudEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lobbies_id_seq")
  @SequenceGenerator(name = "lobbies_id_seq", sequenceName = "lobbies_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false, unique = true)
  private int id;

  @ManyToOne
  @JoinColumn(name = "cafe_id", nullable = false)
  private Cafe cafe;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  @Column(name = "max_participants", nullable = false)
  private Integer maxParticipants;

  @Column(name = "description", length = 200)
  private String description;

  @Column(name = "current_participants")
  private Integer currentParticipants;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinTable(
    name = "lobby_participants",
    joinColumns = @JoinColumn(name = "lobby_id", nullable = false),
    inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false))
  private Set<User> participants;

  public void increaseCurrentParticipants() {currentParticipants++;}
  public void decreaseCurrentParticipants() {currentParticipants--;}

}
