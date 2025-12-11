package org.logistservice.logist.client.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private Long id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String taxNumber;
    private String city;
    private String address;
    private LocalDateTime createdAt;
}




