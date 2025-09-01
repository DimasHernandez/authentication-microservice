package co.com.pragma.jwtadapter;

import co.com.pragma.jwtadapter.config.JwtProperties;
import co.com.pragma.model.auth.gateways.JwtGateway;
import co.com.pragma.model.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JwtProvider implements JwtGateway {

    private final JwtProperties jwtProperties;

    private static final Logger log = Logger.getLogger(JwtProvider.class.getName());

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String generateToken(User user, String userRole) {

        Instant now = Instant.now();
        Date issuedAt = Date.from(now);

        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer("authentication-msvc")
                .setIssuedAt(issuedAt)
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .claim("userId", user.getId())
                .claim("documentNumber", user.getDocumentNumber())
                .claim("email", user.getEmail())
                .claim("name", fullName(user.getName(), user.getSurname()))
                .claim("role", userRole)
                .signWith(getKey(jwtProperties.getSecretKey()))
                .compact();

        log.log(Level.INFO, "Token generado para usuario = {0} rol = {1}", new Object[]{user.getEmail(), userRole});
        return token;
    }

    @Override
    public Boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getKey(jwtProperties.getSecretKey()))
                    .build()
                    .parseClaimsJws(token);

            Date expiration = claimsJws.getBody().getExpiration();
            return expiration.after(new Date());

        } catch (ExpiredJwtException e) {
            log.info("Token is expired");
        } catch (UnsupportedJwtException e) {
            log.info("Token not unsupported");
        } catch (MalformedJwtException e) {
            log.info("Malformed token");
        } catch (SignatureException e) {
            log.info("Invalid signature");
        } catch (IllegalArgumentException e) {
            log.info("Empty or null token");
        }
        return false;
    }

    @Override
    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    @Override
    public String extractUserEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    @Override
    public String extractUserDocumentNumber(String token) {
        return extractAllClaims(token).get("documentNumber", String.class);
    }

    @Override
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(jwtProperties.getSecretKey()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String fullName(String name, String surname) {
        return String.format("%s %s", name, surname);
    }

    private SecretKey getKey(String secretKey) {
        byte[] secretBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
