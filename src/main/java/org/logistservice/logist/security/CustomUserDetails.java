package org.logistservice.logist.security;

import lombok.Getter;
import org.logistservice.logist.user.model.Role;
import org.logistservice.logist.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {
    
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean active;
    
    private CustomUserDetails(Long id, String username, String password, 
                             Collection<? extends GrantedAuthority> authorities, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.active = active;
    }
    
    public static CustomUserDetails fromUser(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(Role::getName)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName.name()))
                .collect(Collectors.toList());
        
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities,
                user.getActive()
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return active;
    }
}





