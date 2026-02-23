package com.Edu_App.controllers; 
import com.Edu_App.DTOs.LoginRequest;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.*; 
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginRequest request) {

        Optional<UserEntity> optionalUser =
                userRepository.findByEmailAndStatus(
                        request.getEmail(),
                        com.Edu_App.domain.entities.UserStatus.ACTIVE
                );

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            if (user.getHashPassword().equals(request.getPassword())) {
                return "home";
            }
        }

        return "login";
    }
}