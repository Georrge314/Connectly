package bg.connectly.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final long EXPIRATION_TIME = 86400000;  // 1 day in milliseconds
    private final Key SECRET_KEY = Keys.hmacShaKeyFor("random_key_secret_secret_key_secret".getBytes());


    //generate token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    //get email for token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    //is token valid
    public Boolean validateToken(String token) {
        return extractEmail(token) != null && !isTokenExpired(token);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
