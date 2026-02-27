package com.Edu_App.controllers;

import com.Edu_App.DTOs.LoginRequest;
import com.Edu_App.DTOs.RegisterRequest;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.services.UserService;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class LoginRestController 
{
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> processLogin(@RequestBody LoginRequest request,
                                        HttpSession session) 
    {
        try 
        {
            UserEntity user = userService.findUserByEmailOrUsername(request.getEmailOrUsername());

            if(passwordEncoder.matches(request.getPassword(), user.getHashPassword()))
            {
                
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userEmail", user.getEmail());
    
                return ResponseEntity.ok().body(Map.of(
                    "message", "Login successful",
                    "username", user.getUsername()
                ));
            } 
            else 
            {
                System.out.println(request.getPassword() + "-" + user.getHashPassword());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password");
            }

        } 
        catch(ResourceNotFoundException exception) 
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> processRegister(@RequestBody RegisterRequest request) 
    {

        try
        {
            UserEntity user = UserEntity.builder().username(request.getUsername())
                                                .email(request.getEmail())
                                                .hashPassword(passwordEncoder.encode(request.getPassword()))
                                                .build();
            this.userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }
        catch(BadRequestException exception)
        {
            return ResponseEntity.badRequest().body("Register failed: " + exception.getMessage());
        }

    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }
}