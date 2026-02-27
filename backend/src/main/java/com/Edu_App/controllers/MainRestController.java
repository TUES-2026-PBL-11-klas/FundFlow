package com.Edu_App.controllers;

import com.Edu_App.DTOs.AccountCreatingDto;
import com.Edu_App.DTOs.AccountDto;
import com.Edu_App.DTOs.CurrencyDto;
import com.Edu_App.DTOs.TransferRequest;
import com.Edu_App.DTOs.DepositWithdrawRequest;
import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.TransferEntity;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.services.AccountService;
import com.Edu_App.services.CurrencyService;
import com.Edu_App.services.TransferService;
import com.Edu_App.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainRestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(HttpSession session) 
    {
        String email = (String) session.getAttribute("userEmail");
        String username = (String) session.getAttribute("username");
        
        if(email == null) 
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
        
        return ResponseEntity.ok(Map.of(
            "email", email,
            "username", username
        ));
    }


    @PostMapping("/accounts/create")
    public ResponseEntity<?> createAccount(@RequestBody AccountCreatingDto request, HttpSession session) 
    {
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null) 
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
        UserEntity user = this.userService.findActiveUserById(userId);
        if(!request.getUseEmail().equals(user.getEmail()))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong email");
        }
        if(!this.passwordEncoder.matches(request.getUserPassword(), user.getHashPassword()))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong password");
        }
        String currencyCode = request.getAccountCurrencyCode();
        try 
        {
            CurrencyEntity currencyEntity = this.currencyService.findCurrencyByCode(currencyCode);

            AccountEntity accountEntity = AccountEntity.builder()
                                                        .balance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                                                        .currency(currencyEntity)
                                                        .iban(this.accountService.generateRandomIban())
                                                        .owner(this.userService.findActiveUserById(userId))
                                                        .build();
            this.accountService.createAccount(accountEntity);
            
            return ResponseEntity.ok("Account created");
        } 
        catch(BadRequestException badRequestException) 
        {
            return ResponseEntity.badRequest().body(badRequestException.getMessage());
        }
        catch(ResourceNotFoundException resourceNotFoundException)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resourceNotFoundException.getMessage());
        }
    }


    @PostMapping("/accounts/deposit")
    @Transactional
    public ResponseEntity<?> processDeposit(@RequestBody DepositWithdrawRequest request,
                                  HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try
        {
            List<AccountEntity> userAccounts = this.accountService.findActiveAccountsByOwner(userId);
            AccountEntity ibanAccount = this.accountService.findActiveAccountByIban(request.getIban());
            if(!userAccounts.contains(ibanAccount))
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This account does not belong to this user.");
            }
            BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
            this.accountService.depositInAccount(ibanAccount.getId(), amount);
            return ResponseEntity.ok(Map.of("message", "succesful deposit"));
        }
        catch(BadRequestException badRException)
        {
            return ResponseEntity.badRequest().body(badRException.getMessage());
        }
        catch(ResourceNotFoundException resourceNotFoundException)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resourceNotFoundException.getMessage());
        }

    }


    @GetMapping("/accounts")
    public ResponseEntity<?> getAccounts(HttpSession session) 
    {
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null) 
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        List<AccountEntity> accounts = this.accountService.findActiveAccountsByOwner(userId);
        List<AccountDto> accountDtos = new ArrayList<>();

        for(AccountEntity entity : accounts) 
        {
            AccountDto dto = this.modelMapper.map(entity, AccountDto.class);
            accountDtos.add(dto);
        }

        return ResponseEntity.ok(accountDtos);
    }
    
    @PostMapping("/withdraw")
    @Transactional
    public ResponseEntity<?> processWithdraw(@RequestBody DepositWithdrawRequest request,
                                  HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try
        {
            List<AccountEntity> userAccounts = this.accountService.findActiveAccountsByOwner(userId);
            AccountEntity ibanAccount = this.accountService.findActiveAccountByIban(request.getIban());
            if(!userAccounts.contains(ibanAccount))
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This account does not belong to this user.");
            }
            BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
            this.accountService.withdrawFromAccount(ibanAccount.getId(), amount);
            return ResponseEntity.ok(Map.of("message", "succesful withdraw"));
        }
        catch(BadRequestException badRException)
        {
            return ResponseEntity.badRequest().body(badRException.getMessage());
        }
        catch(ResourceNotFoundException resourceNotFoundException)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resourceNotFoundException.getMessage());
        }

    }


    @GetMapping("/currencies")
    public ResponseEntity<?> getCurrencies(HttpSession session)
    {
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null) 
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        List<CurrencyEntity> currencies = this.currencyService.getAllCurrencies();
        List<CurrencyDto> currenciesDto = new ArrayList<>();
        for(CurrencyEntity c : currencies)
        {
            CurrencyDto currencyDto = this.modelMapper.map(c, CurrencyDto.class);
            currenciesDto.add(currencyDto);
        }
        return ResponseEntity.ok(currenciesDto);
    }

    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<?> processTransfer(@RequestBody TransferRequest request, 
                                            HttpSession session) 
    {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) 
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired or missing.");
        }
        try
        {
            List<AccountEntity> userAccounts = this.accountService.findActiveAccountsByOwner(userId);
            AccountEntity sender = this.accountService.findActiveAccountByIban(request.getSenderIban());
            if(!userAccounts.contains(sender))
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("this account do not belong to this user.");
            }
            AccountEntity receiverAccount = this.accountService.findActiveAccountByIban(request.getReceiverIban());
            CurrencyEntity currency = this.currencyService.findCurrencyByCode(request.getCurrencyCode());
            TransferEntity transfer = TransferEntity.builder()
                                                    .amount(request.getAmount().setScale(2, RoundingMode.HALF_UP))
                                                    .sender(sender)
                                                    .receiver(receiverAccount)
                                                    .currency(currency)
                                                    .build();
            
            TransferEntity t = this.transferService.createTransfer(transfer);
            this.transferService.executeTransfer(t.getId());
            return ResponseEntity.ok(Map.of("message", "Transfer completed successfully!"));
        }
        catch(BadRequestException badRequestException)
        {
            return ResponseEntity.badRequest().body(badRequestException.getMessage()); 
        }
        catch(ResourceNotFoundException resourceNotFoundException)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resourceNotFoundException.getMessage());
        }
                        

    }



}