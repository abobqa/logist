package org.logistservice.logist.user.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.logistservice.logist.user.model.RoleName;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleUpdateRequest {
    @NotEmpty(message = "Необходимо выбрать хотя бы одну роль")
    private Set<RoleName> roles;
}

