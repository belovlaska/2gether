package ru.ifmo.is.together.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.framework.CrudRepository;

import ru.ifmo.is.together.users.User;

import java.util.Optional;

public interface ReportRepository extends CrudRepository<Report> {
  Page<Report> findAllByResolved(boolean resolved, Pageable pageable);
  Optional<Report> findBySenderAndUser(User sender, User user);
  Optional<Report> findBySenderAndCafe(User sender, Cafe cafe);
}
