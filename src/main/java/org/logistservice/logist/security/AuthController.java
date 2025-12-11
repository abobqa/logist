package org.logistservice.logist.security;

import jakarta.validation.Valid;
import org.logistservice.logist.common.exception.BadRequestException;
import org.logistservice.logist.security.model.JwtAuthenticationResponse;
import org.logistservice.logist.security.model.LoginRequest;
import org.logistservice.logist.security.model.RegisterUserRequest;
import org.logistservice.logist.user.model.Role;
import org.logistservice.logist.user.model.RoleName;
import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.repository.RoleRepository;
import org.logistservice.logist.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    public AuthController(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already taken");
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
                .orElseThrow(() -> new RuntimeException("Role " + selectedRole + " not found"));
        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userDetails);
        
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toSet());
        
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
        
        return ResponseEntity.ok(response);
    }
}





