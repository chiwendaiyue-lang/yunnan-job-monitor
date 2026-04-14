package com.yunnan.jobmonitor.security;

import com.yunnan.jobmonitor.config.AppJwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final AppJwtProperties props;

  public JwtService(AppJwtProperties props) {
    this.props = props;
  }

  public String generateToken(String username) {
    Instant now = Instant.now();
    Instant exp = now.plusMillis(props.expirationMs());
    SecretKey key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return parse(token).getSubject();
  }

  private Claims parse(String token) {
    SecretKey key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
