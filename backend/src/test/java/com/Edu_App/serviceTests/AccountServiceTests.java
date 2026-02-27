package com.Edu_App.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.AccountStatus;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.services.AccountService;
import com.Edu_App.services.CurrencyService;
import com.Edu_App.services.UserService;
@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountServiceTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void testCreateAccount() 
    {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity account = TestData.CreateTestAccountEntity1(user, currency);

        AccountEntity saved = accountService.createAccount(account);

        assertThat(saved).isNotNull();
        assertThat(saved.getIban()).isEqualTo(account.getIban());
        assertThat(saved.getBalance()).isEqualTo(account.getBalance());
        assertThat(saved.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    public void testCreateAccountDuplicateIbanThrows() 
    {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity account1 = TestData.CreateTestAccountEntity1(user, currency);
        AccountEntity account2 = TestData.CreateTestAccountEntity1(user, currency);

        accountService.createAccount(account1);
        assertThrows(BadRequestException.class, () -> accountService.createAccount(account2));
    }

    @Test
    public void testCreateAccountNegativeBalanceThrows() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity account = TestData.CreateTestAccountEntity1(user, currency);
        account.setBalance(BigDecimal.valueOf(-10));
        assertThrows(BadRequestException.class, () -> accountService.createAccount(account));
    }

    @Test
    public void testFindAccountById() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity account = TestData.CreateTestAccountEntity1(user, currency);
        AccountEntity saved = accountService.createAccount(account);

        AccountEntity found = accountService.findAccountById(saved.getId());
        assertThat(found.getIban()).isEqualTo(saved.getIban());
    }

    @Test
    public void testFindActiveAccountByIdThrows() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity account = TestData.CreateTestAccountEntity1(user, currency);
        AccountEntity saved = accountService.createAccount(account);

        accountService.deleteAccountById(saved.getId());

        assertThrows(ResourceNotFoundException.class, () -> accountService.findActiveAccountById(saved.getId()));
    }

    @Test
    public void testDeposit() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());
        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, currency));
        BigDecimal initialBalance = account.getBalance();
        accountService.depositInAccount(account.getId(), BigDecimal.valueOf(50));
        AccountEntity updated = accountService.findAccountById(account.getId());

        assertThat(updated.getBalance().compareTo(initialBalance.add(BigDecimal.valueOf(50))) == 0);
    }

    @Test
    public void testDepositNegativeThrows() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());
        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, currency));

        assertThrows(BadRequestException.class, () -> accountService.depositInAccount(account.getId(),BigDecimal.valueOf(-10)));
    }

    @Test
    public void testWithdraw() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());
        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, currency));
        BigDecimal initialBalance = account.getBalance();
        accountService.withdrawFromAccount(account.getId(), BigDecimal.valueOf(30));
        AccountEntity updated = accountService.findAccountById(account.getId());

        assertThat(updated.getBalance().compareTo(initialBalance.subtract(BigDecimal.valueOf(30))) == 0);
    }

    @Test
    public void testWithdrawTooMuchThrows() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());
        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, currency));
        BigDecimal initialBalance = account.getBalance();
        assertThrows(BadRequestException.class, () -> accountService.withdrawFromAccount(account.getId(), initialBalance.add(BigDecimal.valueOf(200))));
    }

    @Test
    public void testWithdrawNegativeThrows() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());
        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, currency));

        assertThrows(BadRequestException.class, () -> accountService.withdrawFromAccount(account.getId(), BigDecimal.valueOf(-10)));
    }

    @Test
    public void testChangeCurrency() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity oldCurrency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());
        CurrencyEntity newCurrency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity2());

        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, oldCurrency));

        BigDecimal oldBalance = account.getBalance();
        BigDecimal oldRate = oldCurrency.getExchangeRate();
        BigDecimal newRate = newCurrency.getExchangeRate();
        BigDecimal expectedNewBalance = (oldBalance.divide(oldRate, 10, RoundingMode.HALF_UP).multiply(newRate));

        accountService.changeCurrency(account.getId(), newCurrency.getId());
        AccountEntity updated = accountService.findAccountById(account.getId());

        assertThat(updated.getCurrency().getId()).isEqualTo(newCurrency.getId());
        assertThat(updated.getBalance().compareTo(expectedNewBalance) == 0);
    }

    @Test
    public void testChangeCurrencyNonExistingAccountThrows() {
        assertThrows(ResourceNotFoundException.class, () -> accountService.changeCurrency(999, 1));
    }

    @Test
    public void testDeleteAccount() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        CurrencyEntity currency = currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        AccountEntity account = accountService.createAccount(TestData.CreateTestAccountEntity1(user, currency));

        accountService.deleteAccountById(account.getId());
        AccountEntity deleted = accountService.findAccountById(account.getId());

        assertThat(deleted.getStatus()).isEqualTo(AccountStatus.DELETED);
    }

    @Test
    public void testDeleteNonExistingThrows() {
        assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccountById(999));
    }
}