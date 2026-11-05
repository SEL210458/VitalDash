package at.spengergasse.vitaldash.web;

import at.spengergasse.vitaldash.dto.LoginRequest;
import at.spengergasse.vitaldash.dto.LoginResponse;
import at.spengergasse.vitaldash.dto.UserDto;
import at.spengergasse.vitaldash.repository.AppUserRepository;
import at.spengergasse.vitaldash.security.AuthUser;
import at.spengergasse.vitaldash.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AppUserRepository users;

    public AuthController(AuthService authService, AppUserRepository users) {
        this.authService = authService;
        this.users = users;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Benutzername oder Passwort falsch"));
    }

    /** Liefert den angemeldeten Benutzer (z. B. beim Neuladen des Frontends). */
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal AuthUser principal) {
        return users.findById(principal.id())
                .map(UserDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Benutzer existiert nicht mehr"));
    }
}
