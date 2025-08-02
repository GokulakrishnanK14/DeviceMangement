package com.example.invmgnt.invmgnt.SecurityService;

import com.example.invmgnt.invmgnt.Exception.UserNotFoundException;
import com.example.invmgnt.invmgnt.model.User;
import com.example.invmgnt.invmgnt.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {

            User user = userService.getUserByEmail(email);
            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

            return new CustomUserDetails(
                    user.getId(),
                    user.getMail(),
                    user.getPassword(),
                    authorities
            );

        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }
}
