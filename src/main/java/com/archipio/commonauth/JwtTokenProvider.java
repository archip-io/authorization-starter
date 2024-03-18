package com.archipio.commonauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Map;

public class JwtTokenProvider {

  private static final String BEARER_PREFIX = "Bearer ";

  public String resolveTokenHeader(String tokenHeader) {
    if (tokenHeader != null && tokenHeader.startsWith(BEARER_PREFIX)) {
      return tokenHeader.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  public Map<String, Claim> getClaims(String token) {
    DecodedJWT jwt = JWT.decode(token);
    return jwt.getClaims();
  }
}
