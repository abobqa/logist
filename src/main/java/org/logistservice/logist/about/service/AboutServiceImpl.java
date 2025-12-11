package org.logistservice.logist.about.service;

import org.logistservice.logist.about.model.AboutInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AboutServiceImpl implements AboutService {
    
    @Override
    public AboutInfo getAboutInfo() {
        return AboutInfo.builder()
                .authorName("Спехин Д. В.")
                .group("ИД23-1")
                .email("233478@edu.fa.ru")
                .technologies("Java 17, Spring Boot, Spring Security, JPA, PostgreSQL")
                .startDate(LocalDate.of(2025, 10, 21))
                .endDate(LocalDate.of(2025, 12, 17))
                .build();
    }
}

