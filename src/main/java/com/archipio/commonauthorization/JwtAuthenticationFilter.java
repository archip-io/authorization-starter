package com.archipio.commonauthorization;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain filterChain)
      throws IOException, ServletException {
    try {
      var token = jwtTokenProvider.resolveTokenHeader(request.getHeader(AUTHORIZATION));
      var claims = jwtTokenProvider.getClaims(token);
      var userDetails =
          UserDetailsImpl.builder()
              .username(claims.get("username").asString())
              .email(claims.get("email").asString())
              .authorities(claims.get("authorities").asList(String.class))
              .build();
      Authentication auth =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
    } catch (Exception e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      throw e;
    }

    filterChain.doFilter(request, response);
  }
}
