package at.spengergasse.vitaldash.dto;

import at.spengergasse.vitaldash.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        // BCrypt verarbeitet höchstens 72 Byte
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 100) String displayName,
        @NotNull Role role) {
}
