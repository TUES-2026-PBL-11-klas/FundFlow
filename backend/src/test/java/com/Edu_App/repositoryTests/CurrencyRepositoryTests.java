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
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.repositories.CurrencyRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CurrencyRepositoryTests {

    private CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyRepositoryTests(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Test
    public void testSaveAndFindCurrency() {
        CurrencyEntity currency = TestData.CreateTestCurrencyEntity1();
        currencyRepository.save(currency);

        Optional<CurrencyEntity> result = currencyRepository.findById(currency.getCode());

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo(currency.getCode());
        assertThat(result.get().getExchangeRate()).isEqualTo(currency.getExchangeRate());
    }

    @Test
    public void testUpdateAndDeleteCurrency() {
        CurrencyEntity currency = TestData.CreateTestCurrencyEntity1();
        currencyRepository.save(currency);
        String code = currency.getCode();

        currency.setExchangeRate(1.53);
        currencyRepository.save(currency);

        Optional<CurrencyEntity> updated = currencyRepository.findById(code);
        assertThat(updated).isPresent();
        assertThat(updated.get().getExchangeRate()).isEqualTo(1.53);

        currencyRepository.deleteById(code);

        Optional<CurrencyEntity> deleted = currencyRepository.findById(code);
        assertThat(deleted).isEmpty();
    }
}