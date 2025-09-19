package com.example.securityapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private int jwtExpirationMs;

  public String generateJwtToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .setIssuer("security-lab-1")
        .claim("type", "access")
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      var claims =
          Jwts.parserBuilder()
              .setSigningKey(getSigningKey())
              .requireIssuer("security-lab-1")
              .build()
              .parseClaimsJws(authToken);

      String tokenType = claims.getBody().get("type", String.class);
      if (!"access".equals(tokenType)) {
        System.err.println("Invalid token type: " + tokenType);
        return false;
      }

      return true;
    } catch (SecurityException e) {
      System.err.println("Invalid JWT signature: " + e.getMessage());
    } catch (MalformedJwtException e) {
      System.err.println("Invalid JWT token: " + e.getMessage());
    } catch (ExpiredJwtException e) {
      System.err.println("JWT token is expired: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      System.err.println("JWT token is unsupported: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.err.println("JWT claims string is empty: " + e.getMessage());
    }

    return false;
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }
}
