package at.spengergasse.vitaldash.security;

import at.spengergasse.vitaldash.domain.Role;

/** Angemeldeter Benutzer, wie er aus dem JWT gelesen wird (Principal im SecurityContext). */
public record AuthUser(Long id, String username, Role role) {
}
