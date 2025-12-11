package org.logistservice.logist.config;

import org.logistservice.logist.user.model.Role;
import org.logistservice.logist.user.model.RoleName;
import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.repository.RoleRepository;
import org.logistservice.logist.user.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements ApplicationRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        // Ensure all roles exist in database
        // This creates roles: ADMIN, MANAGER, OPERATOR, USER
        System.out.println("Initializing roles in database...");
        for (RoleName roleName : RoleName.values()) {
            try {
                roleRepository.findByName(roleName).orElseGet(() -> {
                    Role role = Role.builder()
                            .name(roleName)
                            .users(new HashSet<>())
                            .build();
                    Role saved = roleRepository.save(role);
                    System.out.println("✓ Created role: " + roleName);
                    return saved;
                });
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // Если constraint не позволяет создать роль, выводим предупреждение
                System.err.println("⚠ WARNING: Could not create role " + roleName + 
                        ". Please update the database constraint 'roles_name_check' to include '" + roleName + "'.");
                System.err.println("Run the following SQL: ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check;");
                System.err.println("ALTER TABLE roles ADD CONSTRAINT roles_name_check CHECK (name IN ('ADMIN', 'MANAGER', 'OPERATOR', 'USER'));");
            } catch (Exception e) {
                System.err.println("✗ Error creating role " + roleName + ": " + e.getMessage());
            }
        }
        System.out.println("Role initialization completed.");
        
        // Create admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .fullName("System Administrator")
                    .email("admin@example.com")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();
            
            userRepository.save(admin);
            System.out.println("Created admin user with username: admin, password: admin");
        }
    }
}





