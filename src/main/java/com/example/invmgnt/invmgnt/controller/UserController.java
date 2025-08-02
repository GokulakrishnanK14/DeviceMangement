package com.example.invmgnt.invmgnt.controller;

import com.example.invmgnt.invmgnt.SecurityService.CustomUserDetails;
import com.example.invmgnt.invmgnt.Util.AuthorizationUtil;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.model.User;
import com.example.invmgnt.invmgnt.model.UserRole;
import com.example.invmgnt.invmgnt.model.UserStatus;
import com.example.invmgnt.invmgnt.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/allUsers")
    public String showAllUsers(@AuthenticationPrincipal CustomUserDetails currentUser,
                               Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            AuthorizationUtil.checkForAdminAccess(currentUser);
            Page<User> userPage = userService.getAllUsers(page);

            List<User> userList;
            if (userPage.hasContent())
                userList = userPage.getContent();
            else
                userList = new ArrayList<User>();

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", userPage.getTotalPages());
            model.addAttribute("userList", userList);

            return "AllUsers";

        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model, e);
        }
    }

    @GetMapping("admin/userrole")
    public String updateUserRole(@AuthenticationPrincipal CustomUserDetails currentUser,
                                 @RequestParam("userRole")UserRole userRole,
                                 Model model){
        try{
            return "redirect:/user/admin/allUsers";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("admin/userstatus")
    public String updateUserStatus(@AuthenticationPrincipal CustomUserDetails currentUser,
                                   @RequestParam("userStatus")UserStatus userStatus,
                                    Model model){
        try{
            return "redirect:/user/admin/allUsers";
        } catch (Exception e) {
            return ExceptionUtil.returnErrPage(model,e);
        }
    }
}
