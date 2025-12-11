package org.logistservice.logist.about.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.about.model.AboutInfo;
import org.logistservice.logist.about.service.AboutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/about")
@RequiredArgsConstructor
public class AboutController {
    
    private final AboutService aboutService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<AboutInfo> getAboutInfo() {
        return ResponseEntity.ok(aboutService.getAboutInfo());
    }
}


