package at.spengergasse.vitaldash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// Die Anmeldung läuft über den eigenen Login-Endpunkt (JWT), daher kein Standard-Benutzer von Spring Boot.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class VitalDashApplication {

    public static void main(String[] args) {
        SpringApplication.run(VitalDashApplication.class, args);
    }
}
