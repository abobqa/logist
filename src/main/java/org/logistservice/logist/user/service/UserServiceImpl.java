package org.logistservice.logist.user.service;

import org.logistservice.logist.common.exception.NotFoundException;
import org.logistservice.logist.user.model.Role;
import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.model.dto.UserRoleUpdateRequest;
import org.logistservice.logist.user.repository.RoleRepository;
import org.logistservice.logist.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public User updateUserRoles(Long userId, UserRoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        Set<Role> newRoles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        
        user.setRoles(newRoles);
        return userRepository.save(user);
    }
}





