package ru.ifmo.is.together.passwordreset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ifmo.is.together.users.User;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  Optional<PasswordResetToken> findByToken(String token);
  Optional<PasswordResetToken> findTopByUserOrderByDateDesc(User user);
}
