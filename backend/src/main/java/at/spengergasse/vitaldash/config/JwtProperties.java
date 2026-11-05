package at.spengergasse.vitaldash.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Einstellungen für die JWT-Ausgabe (Präfix vitaldash.jwt). */
@ConfigurationProperties(prefix = "vitaldash.jwt")
public record JwtProperties(String secret, Duration expiration) {
}
