package org.logistservice.logist.driver.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {
    private Long id;
    private String fullName;
    private String phone;
    private String drivingLicense;
    private Integer experienceYears;
    private Boolean active;
}




