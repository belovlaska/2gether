package ru.ifmo.is.together.common.context;

import jakarta.servlet.*;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class UnlockerFilter implements Filter {

  private final ApplicationLockBean applicationLockBean;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
      chain.doFilter(request, response);
    } finally {
      applicationLockBean.getImportLock().unlock();
    }
  }
}
