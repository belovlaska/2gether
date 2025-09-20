package ru.ifmo.is.together.common.context;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.locks.ReentrantLock;

@Component
public class ApplicationLockBean {
  private final ReentrantLock importLock = new ReentrantLock();

  @Bean
  public ReentrantLock getImportLock() {
    return importLock;
  }
}
