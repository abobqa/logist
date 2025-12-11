package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.common.exception.NotFoundException;
import org.logistservice.logist.user.model.RoleName;
import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.model.dto.UserRoleUpdateRequest;
import org.logistservice.logist.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserPageController {
    
    private final UserService userService;
    
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/ui/users";
        }
        model.addAttribute("user", user);
        return "users/detail";
    }
    
    @GetMapping("/{id}/edit-roles")
    public String showEditRolesForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/ui/users";
        }
        
        Set<RoleName> currentRoles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
        
        UserRoleUpdateRequest request = UserRoleUpdateRequest.builder()
                .roles(currentRoles)
                .build();
        
        model.addAttribute("userId", id);
        model.addAttribute("user", user);
        model.addAttribute("roleUpdate", request);
        model.addAttribute("availableRoles", RoleName.values());
        return "users/edit-roles";
    }
    
    @PostMapping("/{id}/roles")
    public String updateUserRoles(
            @PathVariable Long id,
            @RequestParam(value = "roles", required = false) String[] roleNames,
            RedirectAttributes redirectAttributes) {
        
        try {
            Set<RoleName> roles = new java.util.HashSet<>();
            if (roleNames != null) {
                for (String roleName : roleNames) {
                    try {
                        roles.add(RoleName.valueOf(roleName));
                    } catch (IllegalArgumentException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Неизвестная роль: " + roleName);
                        return "redirect:/ui/users/" + id + "/edit-roles";
                    }
                }
            }
            
            if (roles.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Необходимо выбрать хотя бы одну роль");
                return "redirect:/ui/users/" + id + "/edit-roles";
            }
            
            UserRoleUpdateRequest request = UserRoleUpdateRequest.builder()
                    .roles(roles)
                    .build();
            
            userService.updateUserRoles(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Роли пользователя успешно обновлены");
            return "redirect:/ui/users/" + id;
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            return "redirect:/ui/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении ролей: " + e.getMessage());
            return "redirect:/ui/users/" + id + "/edit-roles";
        }
    }
}

