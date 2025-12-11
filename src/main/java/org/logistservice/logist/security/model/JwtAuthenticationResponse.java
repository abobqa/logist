package org.logistservice.logist.security.model;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthenticationResponse {
    @Builder.Default
    private String tokenType = "Bearer";
    private String token;
    private String username;
    private Set<String> roles;
}





