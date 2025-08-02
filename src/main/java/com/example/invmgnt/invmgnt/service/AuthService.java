package com.example.invmgnt.invmgnt.service;

import com.example.invmgnt.invmgnt.DTO.UserAuthDTO;
import com.example.invmgnt.invmgnt.DTO.UserDTO;
import com.example.invmgnt.invmgnt.Exception.AuthExceptions;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.model.User;
import com.example.invmgnt.invmgnt.model.UserRole;
import com.example.invmgnt.invmgnt.model.UserStatus;
import com.example.invmgnt.invmgnt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private final UserRepository userRepository;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder,
                       UserRepository userRepository){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    /*
    Translate to User object & Pass to User service
     */
    public UserDTO register(UserAuthDTO userAuthDTO){
        try{
            User user = new User();
            user.setName(userAuthDTO.getName());
            user.setMail(userAuthDTO.getMail());

            if(mailAlreadyExists(userAuthDTO)) {
                throw new AuthExceptions("User account already exists " + userAuthDTO.getName() + "  " + userAuthDTO.getMail());
            }
            if(userAuthDTO.getPassword().length() < 8){
                throw new AuthExceptions("Password length should be minimum 8 characters");
            }

            user.setPassword(passwordEncoder.encode(userAuthDTO.getPassword()));
            user.setRole(UserRole.STANDARD);
            user.setStatus(UserStatus.ACTIVE);
            return userService.register(user);
        } catch (Exception e) {
            throw new AuthExceptions(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }

    public boolean mailAlreadyExists(UserAuthDTO userAuthDTO){
        try {
            User user = userService.getUserByEmail(userAuthDTO.getMail());
            return  true;
        }catch (Exception e){
            return false;
        }
    }

}
