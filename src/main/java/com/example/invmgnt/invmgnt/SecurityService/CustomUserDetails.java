package com.example.invmgnt.invmgnt.SecurityService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }


    public String getUsername() { return username;}

    public String getPassword() { return password; }

    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    public boolean isAccountNonExpired() { return true; }

    public boolean isAccountNonLocked() { return true; }

    public boolean isCredentialsNonExpired() { return true; }

    public boolean isEnabled() { return true; }

    public boolean isAdmin(){
        for(GrantedAuthority authority : authorities){
            String role = authority.getAuthority();
            if(role.equals("ADMIN") || role.equals("SUPER_ADMIN"))
                return true;
        }
        return false;
    }

}
