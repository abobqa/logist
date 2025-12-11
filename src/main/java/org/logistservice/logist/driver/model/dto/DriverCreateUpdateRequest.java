package org.logistservice.logist.driver.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverCreateUpdateRequest {
    @NotBlank
    @Size(max = 100)
    private String fullName;
    
    @Size(max = 30)
    private String phone;
    
    @Size(max = 50)
    private String drivingLicense;
    
    @PositiveOrZero
    private Integer experienceYears;
    
    private Boolean active;
}




