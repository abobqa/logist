package org.logistservice.logist.ui.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.logistservice.logist.security.model.RegisterUserRequest;
import org.logistservice.logist.user.model.Role;
import org.logistservice.logist.user.model.RoleName;
import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.repository.RoleRepository;
import org.logistservice.logist.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Set;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class AuthPageController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/ui/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegisterUserRequest());
        model.addAttribute("availableRoles", RoleName.values());
        return "auth/register";
    }

    @PostMapping("/ui/register")
    public String processRegister(
            @Valid @ModelAttribute("user") RegisterUserRequest request,
            BindingResult bindingResult,
            Model model) {

        if (!bindingResult.hasErrors()) {
            if (userRepository.existsByUsername(request.getUsername())) {
                bindingResult.rejectValue("username", "username.exists",
                        "Пользователь с таким логином уже существует");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                bindingResult.rejectValue("email", "email.exists",
                        "Пользователь с таким email уже существует");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("availableRoles", RoleName.values());
            return "auth/register";
        }

        // По умолчанию назначаем роль USER, если не указана
        RoleName requestedRole = request.getRole() != null ? request.getRole() : RoleName.USER;
        
        // Обычные пользователи могут регистрироваться только с ролями USER или OPERATOR
        // ADMIN и MANAGER могут назначаться только администраторами
        final RoleName selectedRole;
        if (requestedRole == RoleName.ADMIN || requestedRole == RoleName.MANAGER) {
            selectedRole = RoleName.USER; // Безопасность: принудительно устанавливаем USER
        } else {
            selectedRole = requestedRole;
        }

        Role userRole = roleRepository.findByName(selectedRole)
                .orElseThrow(() -> new IllegalStateException("Роль " + selectedRole + " не найдена"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}




