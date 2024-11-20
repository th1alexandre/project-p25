package com.picpay.sre.challenge.services;

import java.security.Key;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.picpay.sre.challenge.dtos.AuthClientDto;
import com.picpay.sre.challenge.dtos.AuthTokenDto;
import com.picpay.sre.challenge.entities.AuthClientEntity;
import com.picpay.sre.challenge.repositories.IAuthClientRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final IAuthClientRepository authClientRepository;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public AuthTokenDto generateToken(AuthClientDto authClientDto) {
        AuthClientEntity authClientEntity = authClientRepository.findById(authClientDto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!authClientEntity.getClientSecret().equals(authClientDto.getClientSecret())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return AuthTokenDto.builder()
        .accessToken(generateToken(new HashMap<>(), authClientDto.getClientId()))
        .expiresIn(jwtExpiration)
        .build();
    }

    private String generateToken(Map<String, Object> extraClaims, String clientId) {
        return buildToken(extraClaims, clientId, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String clientId,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(clientId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
