package at.spengergasse.vitaldash.dto;

import java.time.Instant;

public record LoginResponse(String token, Instant expiresAt, UserDto user) {
}
