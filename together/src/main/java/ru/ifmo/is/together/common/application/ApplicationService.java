package ru.ifmo.is.together.common.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.ifmo.is.together.common.caching.RequestCache;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.UserService;

public abstract class ApplicationService {

  @Autowired
  protected UserService userService;

  @RequestCache
  protected User currentUser() {
    try {
      return userService.getCurrentUser();
    } catch (UsernameNotFoundException _ex) {
      return null;
    }
  }
}
