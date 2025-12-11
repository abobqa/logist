package org.logistservice.logist.about.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AboutInfo {
    private String authorName;
    private String group;
    private String email;
    private String technologies;
    private LocalDate startDate;
    private LocalDate endDate;
}

