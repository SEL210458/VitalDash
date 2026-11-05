package at.spengergasse.vitaldash.service;

import at.spengergasse.vitaldash.domain.AppUser;
import at.spengergasse.vitaldash.dto.LoginResponse;
import at.spengergasse.vitaldash.dto.UserDto;
import at.spengergasse.vitaldash.repository.AppUserRepository;
import at.spengergasse.vitaldash.security.JwtService;
import java.time.Instant;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Prüft die Zugangsdaten und stellt ein Token aus; bei falschen Daten leer (ohne Hinweis, welcher Teil falsch war). */
    public Optional<LoginResponse> login(String username, String password) {
        return users.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .map(this::issueToken);
    }

    private LoginResponse issueToken(AppUser user) {
        Instant now = Instant.now();
        return new LoginResponse(jwtService.createToken(user, now), jwtService.expiresAt(now), UserDto.from(user));
    }
}
