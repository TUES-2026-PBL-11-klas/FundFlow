package com.Edu_App.controllers;

import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.domain.entities.UserStatus;
import com.Edu_App.repositories.UserRepository;
import com.Edu_App.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null){
            return "redirect:/login";
        }
        UserEntity user = this.userService.findActiveUserById(userId);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("balance", user.getBalance());
        model.addAttribute("loggedUserEmail", user.getEmail());

        return "home";
    }

    @GetMapping("/withdraw")
    public String showWithdrawPage(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        return "withdraw";
    }

    @PostMapping("/withdraw")
    @Transactional
    public String processWithdraw(@RequestParam BigDecimal amount,
                                  HttpSession session,
                                  Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null){
            return "redirect:/login";
        }
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Invalid amount!");
            return "withdraw";
        }

        if (user.getBalance().compareTo(amount) < 0) {
            model.addAttribute("error", "Not enough balance!");
            return "withdraw";
        }

        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);

        return "redirect:/home";
    }

    @GetMapping("/transfer")
    public String showTransferPage(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        return "transfer";
    }

    @PostMapping("/transfer")
    @Transactional
    public String processTransfer(@RequestParam String email,
                                  @RequestParam BigDecimal amount,
                                  HttpSession session,
                                  Model model) {

        Integer senderId = (Integer) session.getAttribute("userId");
        if (senderId == null) return "redirect:/login";

        UserEntity sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Optional<UserEntity> optionalReceiver = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
        if (optionalReceiver.isEmpty()) {
            model.addAttribute("error", "Receiver not found!");
            return "transfer";
        }

        UserEntity receiver = optionalReceiver.get();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Invalid amount!");
            return "transfer";
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            model.addAttribute("error", "Not enough balance!");
            return "transfer";
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        return "redirect:/home";
    }

    @ModelAttribute("loggedUserEmail")
    public String populateLoggedUserEmail(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null){
            return null;
        }
            return userRepository.findById(userId)
                    .map(UserEntity::getEmail)
                    .orElse(null);
    }
}