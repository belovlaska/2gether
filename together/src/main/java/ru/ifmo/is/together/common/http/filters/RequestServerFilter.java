package ru.ifmo.is.together.common.http.filters;

import lombok.NonNull;
import com.fasterxml.uuid.Generators;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class RequestServerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    var uuid = Generators.timeBasedEpochRandomGenerator().generate().toString();

    request.setAttribute("requestUUID", uuid);
    response.setHeader("X-Response-Uuid", uuid);

    filterChain.doFilter(request, response);
  }
}
