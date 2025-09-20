package ru.ifmo.is.together;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ApplicationRouter {

  private static final List<String> crudResources = Arrays.asList(
    "foods", "hookahs", "drinks"
  );

  public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getSecurityRoutes() {
    return request -> {
      crudResources.forEach(resource ->
        request
          // Access to CRUD resources
          .requestMatchers(HttpMethod.GET, "/api/cafes/*/" + resource + "/**").permitAll() // all users can read data
          .requestMatchers(HttpMethod.POST, "/api/cafes/*/" + resource + "/search").permitAll() // all users can search data
          .requestMatchers(HttpMethod.POST, "/api/cafes/*/" + resource + "/**").hasRole("USER") // only authorized users can create data
          .requestMatchers(HttpMethod.DELETE, "/api/" + resource + "/**").hasRole("USER") // deletion is available only to authors or administrators
      );

      request
        // Only authenticated user can log out
        .requestMatchers("/api/auth/sign-out").authenticated()
        .requestMatchers("/api/auth/resend-confirmation").authenticated()
        // Only authenticated user can get current user
        .requestMatchers("/api/auth/me").authenticated()
        // Everyone can /api/auth/**
        .requestMatchers("/api/auth/**").permitAll()

        // Swagger UI (documentation)
        .requestMatchers("/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**").permitAll()

        // Reports
        .requestMatchers(HttpMethod.POST, "/api/users/*/reports").permitAll() // User can report other users
        .requestMatchers(HttpMethod.POST, "/api/cafes/*/reports").permitAll() // User can report cafe
        .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAnyRole("ADMIN") // Admin can view all reports
        .requestMatchers(HttpMethod.GET, "/api/reports/pending").hasAnyRole("ADMIN") // Admin can view pending report
        .requestMatchers(HttpMethod.POST, "/api/reports/search").hasAnyRole("ADMIN") // Admin can search reports
        .requestMatchers(HttpMethod.PATCH, "/api/reports/**").hasAnyRole("ADMIN") // Admin can update reports
        .requestMatchers(HttpMethod.DELETE, "/api/reports/**").hasAnyRole("ADMIN") // Only admin can delete reports

        // Lobbies
        .requestMatchers(HttpMethod.GET, "/api/users/*/lobbies").permitAll() // Get user's lobbies
        .requestMatchers(HttpMethod.GET, "/api/lobbies/*").permitAll() //get lobbies
//        .requestMatchers(HttpMethod.DELETE, "/api/lobbies/*").hasRole("USER") //Delete lobby
        .requestMatchers(HttpMethod.GET, "/api/cafes/*/lobbies").permitAll() // Get cafe's lobbies
        .requestMatchers(HttpMethod.POST, "/api/cafes/*/lobbies").hasRole("USER") //Add lobby
        .requestMatchers(HttpMethod.GET, "/api/lobbies/*/users").permitAll() // Get users from lobby
        .requestMatchers(HttpMethod.POST, "/api/lobbies/*/users").permitAll() // Add user to lobby
        .requestMatchers(HttpMethod.DELETE, "/api/lobbies/*/users").permitAll() // Delete user from lobby

        // Cafes
        .requestMatchers(HttpMethod.GET, "/api/cafes/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/cafes/*/poster").hasRole("ADMIN")
        .requestMatchers(HttpMethod.POST, "/api/cafes/search").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/cafes").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PATCH, "/api/cafes/*").hasRole("USER")
        .requestMatchers(HttpMethod.DELETE, "/api/cafes/*").hasRole("USER")

        // User roles
        .requestMatchers(HttpMethod.POST, "/api/users/*/roles").hasAnyRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/users/*/roles").hasAnyRole("ADMIN")

        // Users
        .requestMatchers(HttpMethod.POST, "/api/users/photo").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/users/search").permitAll()
        .requestMatchers(HttpMethod.PATCH, "/api/users/**").authenticated()
        .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated();

      request
        // Any other request must be authenticated
        .anyRequest().authenticated();
    };
  }
}
