package com.example.invmgnt.invmgnt.controller;

import com.example.invmgnt.invmgnt.SecurityService.CustomUserDetails;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Home {
    @GetMapping(value = {"/","/home"}, produces = "text/html")
    String home(Model model, @AuthenticationPrincipal CustomUserDetails currentUser){
        try{
            if(currentUser.isAdmin()){
                model.addAttribute("isAdmin",true);
            }
            return "Home";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }

    }
}
