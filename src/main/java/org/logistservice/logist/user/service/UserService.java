package org.logistservice.logist.user.service;

import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.model.dto.UserRoleUpdateRequest;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User save(User user);
    void deleteById(Long id);
    User updateUserRoles(Long userId, UserRoleUpdateRequest request);
}





