package com.example.invmgnt.invmgnt.Util;

import com.example.invmgnt.invmgnt.Exception.AccessDeniedException;
import com.example.invmgnt.invmgnt.SecurityService.CustomUserDetails;

public class AuthorizationUtil {
    public static void checkForAdminAccess(CustomUserDetails curUser){
        try{
            if(!curUser.isAdmin())
                throw new AccessDeniedException("You are not admin...");
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }
}
