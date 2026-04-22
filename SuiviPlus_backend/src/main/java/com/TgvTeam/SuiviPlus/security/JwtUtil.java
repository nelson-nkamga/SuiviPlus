package com.TgvTeam.SuiviPlus.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key signingKey;

    /**
     * Initialisation de la clé une seule fois
     */
    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Générer un token JWT
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extraire l'email depuis le token
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Vérifier si le token est valide
     */
    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expiré");
        } catch (UnsupportedJwtException e) {
            System.out.println("Token non supporté");
        } catch (MalformedJwtException e) {
            System.out.println("Token mal formé");
        } catch (SignatureException e) {
            System.out.println("Signature invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("Token vide ou null");
        }
        return false;
    }

    /**
     * Extraire les informations (claims)
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}