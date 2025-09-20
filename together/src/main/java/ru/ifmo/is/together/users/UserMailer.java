package ru.ifmo.is.together.users;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import ru.ifmo.is.together.passwordreset.PasswordResetToken;
import ru.ifmo.is.together.verificationtokens.VerificationToken;

import java.util.Date;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserMailer {

  @Value("${app.static-resources.host}")
  private final String staticResourcesHost;

  @Value("${app.mail.links.confirm_user}")
  private final String confirmationLinkTemplate;

  @Value("${app.mail.links.password_reset}")
  private final String passwordResetLinkTemplate;

  @Value("${spring.mail.username}")
  private final String sender;

  private final MessageSource messages;
  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  @Async
  public void sendPasswordResetInstructions(User user, Locale locale, PasswordResetToken passwordResetToken) {
    var title = messages.getMessage("message.mail.password_reset.title", null, locale);
    var passwordResetLink = passwordResetLinkTemplate + passwordResetToken.getToken();

    var model = new Context(locale);
    model.setVariable("user", user);
    model.setVariable("logo", mailLogo());
    model.setVariable("passwordResetLink", passwordResetLink);

    var htmlContent = templateEngine.process("auth_mail/password_reset", model);
    var textContent = templateEngine.process("auth_mail/password_reset.txt", model);
    sendMail(user, title, htmlContent, textContent);
  }

  @Async
  public void sendConfirmationLink(User user, Locale locale, VerificationToken verificationToken, boolean newUser) {
    var action = newUser  ? "welcome" : "confirm_user";
    var title = messages.getMessage("message.mail." + action + ".title", null, locale);
    var confirmationUrl = confirmationLinkTemplate + verificationToken.getToken();

    var model = new Context(locale);
    model.setVariable("user", user);
    model.setVariable("logo", mailLogo());
    model.setVariable("confirmationLink", confirmationUrl);

    var htmlContent = templateEngine.process("auth_mail/" + action, model);
    var textContent = templateEngine.process("auth_mail/" + action + ".txt", model);
    sendMail(user, title, htmlContent, textContent);
  }

  private void sendMail(User user, String title, String htmlContent, String textContent) {
    MimeMessagePreparator preparator = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
      message.setFrom(sender);
      message.setTo(user.getEmail());
      message.setSentDate(new Date());
      message.setSubject(title);
      message.setText(textContent, htmlContent); // text and html
    };

    mailSender.send(preparator);
  }

  private String mailLogo() {
    return staticResourcesHost + "/together.png";
  }
}
