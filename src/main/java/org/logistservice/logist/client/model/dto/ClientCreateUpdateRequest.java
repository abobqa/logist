package org.logistservice.logist.client.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreateUpdateRequest {
    @NotBlank
    @Size(max = 150)
    private String name;
    
    @Size(max = 100)
    private String contactPerson;
    
    @Size(max = 30)
    private String phone;
    
    @Email
    @Size(max = 100)
    private String email;
    
    @Size(max = 20)
    private String taxNumber;
    
    @Size(max = 100)
    private String city;
    
    @Size(max = 255)
    private String address;
    
    private Boolean active;
}




