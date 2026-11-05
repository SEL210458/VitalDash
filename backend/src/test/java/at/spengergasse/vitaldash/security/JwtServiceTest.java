package at.spengergasse.vitaldash.security;

import static org.assertj.core.api.Assertions.assertThat;

import at.spengergasse.vitaldash.config.JwtProperties;
import at.spengergasse.vitaldash.domain.AppUser;
import at.spengergasse.vitaldash.domain.Role;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private static final String SECRET = "test-geheimnis-test-geheimnis-test-geheimnis-12345";

    private final JwtService service = new JwtService(new JwtProperties(SECRET, Duration.ofHours(1)));

    private AppUser arzt() {
        AppUser user = new AppUser("arzt", "hash", "Dr. Demo", Role.ARZT);
        ReflectionTestUtils.setField(user, "id", 7L);
        return user;
    }

    @Test
    void tokenEnthaeltBenutzerUndRolle() {
        String token = service.createToken(arzt(), Instant.now());

        AuthUser parsed = service.parse(token).orElseThrow();

        assertThat(parsed.id()).isEqualTo(7L);
        assertThat(parsed.username()).isEqualTo("arzt");
        assertThat(parsed.role()).isEqualTo(Role.ARZT);
    }

    @Test
    void abgelaufenesTokenWirdAbgewiesen() {
        String token = service.createToken(arzt(), Instant.now().minus(Duration.ofHours(2)));

        assertThat(service.parse(token)).isEmpty();
    }

    @Test
    void manipuliertesTokenWirdAbgewiesen() {
        String token = service.createToken(arzt(), Instant.now());
        JwtService other = new JwtService(new JwtProperties("anderes-geheimnis-anderes-geheimnis-123456789", Duration.ofHours(1)));

        assertThat(other.parse(token)).isEmpty();
        assertThat(service.parse(token + "x")).isEmpty();
    }

    @Test
    void unsinnigerWertWirdAbgewiesen() {
        assertThat(service.parse("kein-token")).isEmpty();
    }
}
