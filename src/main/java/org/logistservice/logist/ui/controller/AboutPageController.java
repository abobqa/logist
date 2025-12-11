package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.about.model.AboutInfo;
import org.logistservice.logist.about.service.AboutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/about")
@RequiredArgsConstructor
public class AboutPageController {
    
    private final AboutService aboutService;
    
    @GetMapping
    public String about(Model model) {
        AboutInfo about = aboutService.getAboutInfo();
        model.addAttribute("about", about);
        return "about";
    }
}




