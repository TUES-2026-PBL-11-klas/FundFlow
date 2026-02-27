package com.Edu_App.services;

import java.math.BigDecimal;
import java.util.List;

import com.Edu_App.domain.entities.AccountEntity;

public interface AccountService {
    public AccountEntity createAccount(AccountEntity AccountE);
    public AccountEntity findAccountById(Integer id);
    public AccountEntity findAccountByIban(String iban);
    public AccountEntity findActiveAccountById(Integer id);
    public AccountEntity findActiveAccountByIban(String iban);
    public List<AccountEntity> getAllAccounts();
    public List<AccountEntity> getAllActiveAccounts();
    public List<AccountEntity> findActiveAccountsByOwner(Integer userId);
    public void depositInAccount(Integer id, BigDecimal amount);
    public void withdrawFromAccount(Integer id, BigDecimal amount);
    public void changeCurrency(Integer id, Integer newCurrencyId);
    public void deleteAccountById(Integer id);
    public String generateRandomIban();
}
