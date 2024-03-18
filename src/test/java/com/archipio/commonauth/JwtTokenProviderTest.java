package com.archipio.commonauth;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

  @Test
  void resolveTokenHeader_whenTokenHeaderIsValid_thenReturnToken() {
    // Prepare
    final var tokenHeader = "Bearer Token";
    final var token = "Token";

    // Do
    var actual = jwtTokenProvider.resolveTokenHeader(tokenHeader);

    // Check
    assertThat(actual).isEqualTo(token);
  }

  @Test
  void resolveTokenHeader_whenTokenHeaderHasInvalidPrefix_thenReturnNull() {
    // Prepare
    final var tokenHeader = "Basic Token";

    // Do
    var actual = jwtTokenProvider.resolveTokenHeader(tokenHeader);

    // Check
    assertThat(actual).isNull();
  }

  @Test
  void getClaims_whenTokenIsValid_whenClaimsIsValid() {
    // Prepare
    final var username = "user";
    final var email = "user@mail.ru";
    final var authorities = List.of("AUTHORITY_1", "AUTHORITY_2", "AUTHORITY_3");
    final var secret = "53A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A75327854";
    final var tokenTtl = 7 * 24 * 60 * 60 * 1000L;
    final var expiration = new Date(System.currentTimeMillis() - tokenTtl);
    final var token =
        createToken(
            secret,
            username,
            Map.of("username", username, "email", email, "authorities", authorities),
            expiration);

    // Do
    var claims = jwtTokenProvider.getClaims(token);

    // Check
    assertThat(claims.get("username").asString()).isEqualTo(username);
    assertThat(claims.get("email").asString()).isEqualTo(email);
    assertThat(claims.get("authorities").asList(String.class))
        .containsExactlyInAnyOrderElementsOf(authorities);
  }

  private Algorithm getAlgorithm(String secret) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Algorithm.HMAC256(keyBytes);
  }

  private String createToken(
      String secret, String subject, Map<String, ?> claims, Date expiration) {
    return JWT.create()
        .withSubject(subject)
        .withIssuedAt(new Date())
        .withExpiresAt(expiration)
        .withPayload(claims)
        .sign(getAlgorithm(secret));
  }
}
