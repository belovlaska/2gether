package ru.ifmo.is.together.lobby;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.application.ApplicationService;
import ru.ifmo.is.together.common.errors.ResourceAlreadyExists;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.lobby.dto.LobbyCreateDto;
import ru.ifmo.is.together.lobby.dto.LobbyDto;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.UserMapper;

import ru.ifmo.is.together.users.dto.UserDto;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LobbyService extends ApplicationService {

  private static final Logger logger = LoggerFactory.getLogger(LobbyService.class);

  private final LobbyMapper mapper;
  private final LobbyPolicy policy;
  private final LobbyRepository repository;
  private final LobbySpecification specification;
  private final UserMapper userMapper;

  public Optional<Lobby> findById(int id) {
    return repository.findById(id);
  }

  public Page<LobbyDto> getCafeLobbies(Cafe cafe, Pageable pageable) {
    policy.showAll(currentUser());

    var lobbiesInCafeSpecification = specification.inCafe(cafe);
    return repository
      .findAll(lobbiesInCafeSpecification, pageable)
      .map(mapper::map);
  }

  @Transactional
  public LobbyDto addUser(int id) throws Exception {
    var lobby = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));

    if (lobby.getParticipants().contains(currentUser())) {
      throw new ResourceAlreadyExists("You already joined this lobby");
    }

    lobby.getParticipants().add(currentUser());
    lobby.increaseCurrentParticipants();
    return mapper.map(repository.save(lobby));
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public boolean deleteUser(int id) {
    var lobby = repository.findById(id);
    return lobby.map(l -> {
      if (!l.getParticipants().contains(currentUser())) {
        throw new ResourceNotFoundException("You are not in this lobby");
      }
      l.getParticipants().remove(currentUser());
      l.decreaseCurrentParticipants();
      if (l.getCurrentParticipants() == 0) {
        repository.delete(l);
      }
      return true;
    }).orElse(false);
  }

  public Set<UserDto> getParticipants(Lobby lobby, Pageable pageable) {
    return lobby.getParticipants().stream().map(userMapper::map).collect(Collectors.toSet());
  }

  public LobbyDto getById(int id) {
    var lobby = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lobby Not Found: " + id));
    return mapper.map(lobby);
  }

  public Page<LobbyDto> getUserLobbies(User user, Pageable pageable) {
    policy.showAll(currentUser());

    var lobbies = repository.findAll(specification.hasUser(user), pageable);
    return lobbies.map(mapper::map);
  }

  @Transactional
  public LobbyDto create(LobbyCreateDto dto, Cafe cafe) {
    policy.create(currentUser());


    var lobby = mapper.map(dto);
    var initialParticipants = new HashSet<User>();
    initialParticipants.add(currentUser());
    lobby.setParticipants(initialParticipants);
    lobby.setCurrentParticipants(1);
    lobby.setCafe(cafe);

    repository.save(lobby);
    return mapper.map(lobby);
  }

//  @Transactional(isolation = Isolation.REPEATABLE_READ)
//  public boolean delete(int id) {
//    var lobby = repository.findById(id);
//    return lobby.map(l -> {
//      policy.delete(currentUser(), l);
//      repository.delete(l);
//      return true;
//    }).orElse(false);
//  }
}
