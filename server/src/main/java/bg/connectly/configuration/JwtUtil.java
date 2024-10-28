package bg.connectly.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "random_key_secret";
    private final long EXPIRATION_TIME = 86400000;  // 1 day in milliseconds

    //create token from username
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    //get username for token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    //is token valid
    public Boolean validateToken(String token) {
        return extractUsername(token) != null && !isTokenExpired(token);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
