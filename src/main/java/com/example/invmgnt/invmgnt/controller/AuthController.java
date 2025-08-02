package com.example.invmgnt.invmgnt.controller;


import com.example.invmgnt.invmgnt.DTO.UserAuthDTO;
import com.example.invmgnt.invmgnt.Util.ExceptionUtil;
import com.example.invmgnt.invmgnt.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private AuthService authService;
    private AuthenticationManager authenticationManager;
    public AuthController(AuthService authService, AuthenticationManager authenticationManager){
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping(value="/signup", produces = "text/html")
    public String showSignUpForm(Model model) {
        model.addAttribute("userAuthDTO", new UserAuthDTO());
        return "Signup";
    }

    @PostMapping("/signup")
    public String processSignUp(@ModelAttribute("userAuthDTO")UserAuthDTO userAuthDTO,
                                Model model,
                                HttpServletRequest request){
        try{
            authService.register(userAuthDTO);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userAuthDTO.getMail(),userAuthDTO.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            System.out.println("Is authenticated? " + authentication.isAuthenticated());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);


            return "redirect:/home";
        }catch (Exception e){
            return ExceptionUtil.returnErrPage(model,e);
        }
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return "Login";
    }

}
