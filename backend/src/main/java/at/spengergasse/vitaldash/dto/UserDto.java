package at.spengergasse.vitaldash.dto;

import at.spengergasse.vitaldash.domain.AppUser;
import at.spengergasse.vitaldash.domain.Role;

public record UserDto(Long id, String username, String displayName, Role role) {

    public static UserDto from(AppUser user) {
        return new UserDto(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
    }
}
