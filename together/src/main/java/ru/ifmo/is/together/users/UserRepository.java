package ru.ifmo.is.together.users;

import org.springframework.stereotype.Repository;

import ru.ifmo.is.together.common.framework.CrudRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
