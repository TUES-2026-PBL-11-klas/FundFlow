package com.Edu_App.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.AccountStatus;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.repositories.AccountRepository;
import com.Edu_App.services.AccountService;
import com.Edu_App.services.CurrencyService;
import com.Edu_App.services.UserService;
@Service
public class AccountServiceImpl implements AccountService{

    private AccountRepository accountRepository;
    private UserService userService;
    private CurrencyService currencyService;
    public AccountServiceImpl(AccountRepository accountR, UserService userS, CurrencyService currencyS)
    {
        this.accountRepository = accountR;
        this.userService = userS;
        this.currencyService = currencyS;
    }

    @Override
    public AccountEntity createAccount(AccountEntity accountE)
    {
        if(accountRepository.findByIban(accountE.getIban()).isPresent()) 
        {
            throw new BadRequestException("Account with this iban already exists");
        }
        UserEntity owner = userService.findActiveUserById(accountE.getOwner().getId());
        CurrencyEntity currency = currencyService.findCurrencyById(accountE.getCurrency().getId());
        if(accountE.getBalance() < 0) 
        {
            throw new BadRequestException("Initial balance cannot be negative");
        }
        accountE.setOwner(owner);
        accountE.setCurrency(currency);
        if(accountE.getStatus() == null) 
        {
            accountE.setStatus(AccountStatus.ACTIVE);
        }
        return this.accountRepository.save(accountE);
    }

    @Override
    public AccountEntity findAccountById(Integer id)
    {
        Optional<AccountEntity> account = this.accountRepository.findById(id);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("account with this id not found");
        }
        return account.get();
    }

    @Override
    public AccountEntity findAccountByIban(String iban)
    {
        Optional<AccountEntity> account = this.accountRepository.findByIban(iban);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("account with this iban not found");
        }
        return account.get();
    }

    @Override
    public AccountEntity findActiveAccountById(Integer id)
    {
        Optional<AccountEntity> account = this.accountRepository.findByIdAndStatus(id, AccountStatus.ACTIVE);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("account with this id not found");
        }
        return account.get();
    }

    @Override
    public AccountEntity findActiveAccountByIban(String iban)
    {
        Optional<AccountEntity> account = this.accountRepository.findByIbanAndStatus(iban, AccountStatus.ACTIVE);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("account with this iban not found");
        }
        return account.get();
    }

    @Override
    public List<AccountEntity> getAllAccounts()
    {
        Iterable<AccountEntity> accounts = this.accountRepository.findAll();
        List<AccountEntity> accountlist = new ArrayList<>();
        accounts.forEach(accountlist::add);
        return accountlist;
    }

    @Override
    public List<AccountEntity> getAllActiveAccounts()
    {
        return this.accountRepository.findAllByStatus(AccountStatus.ACTIVE);
    }

    @Override
    public List<AccountEntity> findActiveAccountsByOwner(Integer userId)
    {
        UserEntity userEntity = this.userService.findActiveUserById(userId);
        return this.accountRepository.findAllByOwnerAndStatus(userEntity, AccountStatus.ACTIVE);
    }

    @Override
    public void depositInAccount(Integer id, double amount)
    {
        Optional<AccountEntity> account = this.accountRepository.findByIdAndStatus(id, AccountStatus.ACTIVE);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("this account does not exist");
        }
        if(amount < 0)
        {
            throw new BadRequestException("amount cannot be negative");
        }
        double currentBalance = account.get().getBalance();
        account.get().setBalance(currentBalance + amount);
        this.accountRepository.save(account.get());

    }

    @Override
    public void withdrawFromAccount(Integer id, double amount)
    {
        Optional<AccountEntity> account = this.accountRepository.findByIdAndStatus(id, AccountStatus.ACTIVE);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("this account does not exist");
        }
        if(amount < 0)
        {
            throw new BadRequestException("amount cannot be negative");
        }
        double currentBalance = account.get().getBalance();
        if(amount > currentBalance)
        {
            throw new BadRequestException("You cannot withdraw more than the current balance");
        }
        account.get().setBalance(currentBalance - amount);
        this.accountRepository.save(account.get());
    }

    @Override
    public void changeCurrency(Integer id, Integer newCurrencyId)
    {
        Optional<AccountEntity> account = this.accountRepository.findByIdAndStatus(id, AccountStatus.ACTIVE);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("this account does not exist");
        }
        CurrencyEntity newCurrencyEntity = this.currencyService.findCurrencyById(newCurrencyId);
        double oldExchangeRate = account.get().getCurrency().getExchangeRate();
        double newExchangeRate = newCurrencyEntity.getExchangeRate();
        double balance = account.get().getBalance();
        account.get().setBalance((balance/oldExchangeRate) * newExchangeRate);
        account.get().setCurrency(newCurrencyEntity);
        this.accountRepository.save(account.get());
    }

    @Override
    public void deleteAccountById(Integer id)
    {
        Optional<AccountEntity> account = this.accountRepository.findByIdAndStatus(id, AccountStatus.ACTIVE);
        if(!account.isPresent())
        {
            throw new ResourceNotFoundException("this account does not exist");
        }
        account.get().setStatus(AccountStatus.DELETED);
        this.accountRepository.save(account.get());
    }


}
