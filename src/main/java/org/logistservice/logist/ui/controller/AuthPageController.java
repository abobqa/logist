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
        // Показываем только OPERATOR и MANAGER, исключаем USER и ADMIN
        model.addAttribute("availableRoles", new RoleName[]{RoleName.OPERATOR, RoleName.MANAGER});
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
            model.addAttribute("availableRoles", new RoleName[]{RoleName.OPERATOR, RoleName.MANAGER});
            return "auth/register";
        }

        // Пользователи могут регистрироваться только с ролями OPERATOR или MANAGER
        // ADMIN не может быть назначена при регистрации
        final RoleName selectedRole;
        RoleName requestedRole = request.getRole();
        if (requestedRole == null || requestedRole == RoleName.ADMIN || requestedRole == RoleName.USER) {
            selectedRole = RoleName.OPERATOR; // По умолчанию OPERATOR
        } else if (requestedRole == RoleName.MANAGER || requestedRole == RoleName.OPERATOR) {
            selectedRole = requestedRole;
        } else {
            selectedRole = RoleName.OPERATOR; // Безопасность: принудительно устанавливаем OPERATOR
        }

        // Пытаемся найти роль, если не найдена - создаем её
        Role userRole = roleRepository.findByName(selectedRole).orElseGet(() -> {
            Role newRole = Role.builder()
                    .name(selectedRole)
                    .users(new java.util.HashSet<>())
                    .build();
            return roleRepository.save(newRole);
        });

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




