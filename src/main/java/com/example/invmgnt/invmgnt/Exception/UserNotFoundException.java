package com.example.invmgnt.invmgnt.Exception;

public class UserNotFoundException extends  RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
