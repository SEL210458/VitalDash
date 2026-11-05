package at.spengergasse.vitaldash.service;

import at.spengergasse.vitaldash.domain.AppUser;
import at.spengergasse.vitaldash.dto.CreateUserRequest;
import at.spengergasse.vitaldash.dto.UserDto;
import at.spengergasse.vitaldash.repository.AppUserRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return users.findAll(Sort.by("username")).stream().map(UserDto::from).toList();
    }

    @Transactional
    public UserDto create(CreateUserRequest request) {
        if (users.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzername ist bereits vergeben");
        }
        AppUser user = new AppUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.displayName(),
                request.role());
        return UserDto.from(users.save(user));
    }
}
