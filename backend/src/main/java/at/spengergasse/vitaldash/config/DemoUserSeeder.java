package at.spengergasse.vitaldash.config;

import at.spengergasse.vitaldash.domain.AppUser;
import at.spengergasse.vitaldash.domain.Role;
import at.spengergasse.vitaldash.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Legt für die Entwicklung und den Democontent zwei Benutzer an (arzt, pflege),
 * solange die Tabelle app_user leer ist. Abschalten mit SEED_DEMO_USERS=false.
 */
@Component
@ConditionalOnProperty(name = "vitaldash.seed-demo-users", havingValue = "true")
public class DemoUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoUserSeeder.class);

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String demoPassword;

    public DemoUserSeeder(AppUserRepository users, PasswordEncoder passwordEncoder,
                          @Value("${vitaldash.demo-password}") String demoPassword) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.demoPassword = demoPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.count() > 0) {
            return;
        }
        String hash = passwordEncoder.encode(demoPassword);
        users.save(new AppUser("arzt", hash, "Dr. Demo Arzt", Role.ARZT));
        users.save(new AppUser("pflege", hash, "Demo Pflegekraft", Role.PFLEGEKRAFT));
        log.info("Demo-Benutzer 'arzt' und 'pflege' angelegt (Passwort aus vitaldash.demo-password)");
    }
}
