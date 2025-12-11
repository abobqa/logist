package org.logistservice.logist.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.logistservice.logist.user.model.RoleName;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String fullName;
    
    @Email
    @NotBlank
    private String email;
    
    private RoleName role;
}





