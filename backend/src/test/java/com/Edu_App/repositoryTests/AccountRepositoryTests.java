package com.Edu_App.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.repositories.AccountRepository;
import com.Edu_App.repositories.CurrencyRepository;
import com.Edu_App.repositories.UserRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountRepositoryTests {
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private CurrencyRepository currencyRepository;

    @Autowired
    public AccountRepositoryTests(AccountRepository accountR, UserRepository userR, CurrencyRepository currencyR)
    {
        this.accountRepository = accountR;
        this.userRepository = userR;
        this.currencyRepository = currencyR;
    }

    @Test
    public void testSaveAndFindUser()
    {
        UserEntity user1 = TestData.CreateTestUserEntity1();
        CurrencyEntity currency1 = TestData.CreateTestCurrencyEntity1();
        this.userRepository.save(user1);
        this.currencyRepository.save(currency1);
        AccountEntity account = TestData.CreateTestAccountEntity1(user1, currency1);
        accountRepository.save(account);
        Optional<AccountEntity> result = accountRepository.findById(account.getIban());
        assertThat(result).isPresent();
        assertThat(result.get().getBalance()).isEqualTo(account.getBalance());
        assertThat(result.get().getOwner().getUsername()).isEqualTo(account.getOwner().getUsername());
        assertThat(result.get().getOwner().getId()).isEqualTo(user1.getId());
        assertThat(result.get().getCurrency().getCode()).isEqualTo(currency1.getCode());
    }

    @Test
    public void testUpdateAndDeleteAccount() {
        UserEntity user1 = TestData.CreateTestUserEntity1();
        CurrencyEntity currency1 = TestData.CreateTestCurrencyEntity1();
        UserEntity user = userRepository.save(user1);
        CurrencyEntity currency = currencyRepository.save(currency1);

        AccountEntity account = TestData.CreateTestAccountEntity1(user, currency);
        accountRepository.save(account);
        String iban = account.getIban();

        double newBalance = 5000.50;
        account.setBalance(newBalance);
        accountRepository.save(account);

        Optional<AccountEntity> updatedResult = accountRepository.findById(iban);
        assertThat(updatedResult).isPresent();
        assertThat(updatedResult.get().getBalance()).isEqualTo(newBalance);

        accountRepository.deleteById(iban);

        Optional<AccountEntity> deletedResult = accountRepository.findById(iban);
        assertThat(deletedResult).isEmpty();
    }
}
