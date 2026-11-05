package at.spengergasse.vitaldash.security;

import at.spengergasse.vitaldash.config.JwtProperties;
import at.spengergasse.vitaldash.domain.AppUser;
import at.spengergasse.vitaldash.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/** Erstellt und prüft die signierten JWT (HS256). */
@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_UID = "uid";

    private final SecretKey key;
    private final Duration expiration;

    public JwtService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expiration = properties.expiration();
    }

    public String createToken(AppUser user, Instant issuedAt) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim(CLAIM_UID, user.getId())
                .claim(CLAIM_ROLE, user.getRole().name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt(issuedAt)))
                .signWith(key)
                .compact();
    }

    public Instant expiresAt(Instant issuedAt) {
        return issuedAt.plus(expiration);
    }

    /** Liefert den Benutzer aus einem gültigen Token, bei ungültiger Signatur oder abgelaufenem Token leer. */
    public Optional<AuthUser> parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Number uid = claims.get(CLAIM_UID, Number.class);
            Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
            return Optional.of(new AuthUser(uid == null ? null : uid.longValue(), claims.getSubject(), role));
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
