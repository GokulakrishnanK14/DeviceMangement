package com.example.invmgnt.invmgnt.service;

import com.example.invmgnt.invmgnt.DTO.UserDTO;
import com.example.invmgnt.invmgnt.Exception.UserNotFoundException;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.model.User;
import com.example.invmgnt.invmgnt.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService{
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;

    }
    /*
    Add new user to UserTable
     */
    public UserDTO register(User user){
        userRepository.save(user);
        return UserDTO.UserEntityToDTO(user);
    }
    /*
    Find user by mail id
     */
    public User getUserByEmail(String email){
        Optional<User> optionalUser = userRepository.findByMail(email);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }
        else{
            String msg = "User with Email -  "+email+" not found";
            throw new UserNotFoundException(msg);
        }
    }

    public User getUserById(Long id){
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if(optionalUser.isPresent()){
                return optionalUser.get();
            }else{
                throw new UserNotFoundException("User not found");
            }
        } catch (Exception e) {
            String msg = "User with Id "+id+" not found";
            throw new UserNotFoundException(msg);
        }
    }

    public Page<User> getAllUsers(int page){
        try{
            Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.ASC,"name"));
            Page<User> userPage = userRepository.findAllByOrderByNameAsc(pageable);
            return userPage;
        } catch (Exception e) {
            throw new UserNotFoundException(ExceptionUtil.constructExpMsg(e,e.getMessage()));
        }
    }
}
