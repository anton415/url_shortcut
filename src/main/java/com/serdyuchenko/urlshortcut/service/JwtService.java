package com.serdyuchenko.urlshortcut.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.serdyuchenko.urlshortcut.model.Site;
import com.serdyuchenko.urlshortcut.security.AuthenticatedSite;

/**
 * Создает и валидирует JWT для сайтов API.
 */
@Service
public class JwtService {

    private final Key signingKey;

    private final long expirationMillis;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMillis) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    /**
     * Создает JWT для авторизованного сайта.
     *
     * @param site авторизованный сайт
     * @return подписанный JWT
     */
    public String generateToken(Site site) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + expirationMillis);
        return Jwts.builder()
                .setSubject(site.getLogin())
                .claim("siteId", site.getId())
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Извлекает principal сайта из валидного JWT.
     *
     * @param token JWT из заголовка Authorization
     * @return principal авторизованного сайта
     */
    public AuthenticatedSite parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Number siteId = claims.get("siteId", Number.class);
        return new AuthenticatedSite(siteId.longValue(), claims.getSubject());
    }
}
