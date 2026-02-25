package com.Edu_App.controllers;

import com.Edu_App.DTOs.LoginRequest;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.domain.entities.UserStatus;
import com.Edu_App.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginRequest request,
                               HttpSession session,
                               Model model) {

        Optional<UserEntity> optionalUser =
                userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            if (passwordEncoder.matches(request.getPassword(), user.getHashPassword())) {
                session.setAttribute("userId", user.getId());
                return "redirect:/home";
            }
        }

        model.addAttribute("error", "Invalid email or password!");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute LoginRequest request,
                                  Model model) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }

        UserEntity newUser = UserEntity.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .hashPassword(passwordEncoder.encode(request.getPassword()))
                .balance(BigDecimal.ZERO)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(newUser);

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}