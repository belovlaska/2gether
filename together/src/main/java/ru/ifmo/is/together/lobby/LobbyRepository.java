package ru.ifmo.is.together.lobby;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ifmo.is.together.common.framework.CrudRepository;
import ru.ifmo.is.together.users.User;


import java.util.List;
import java.util.Optional;

public interface LobbyRepository extends CrudRepository<Lobby> {


}
