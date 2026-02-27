package com.Edu_App.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.repositories.CurrencyRepository;
@ActiveProfiles("test")
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

        Optional<CurrencyEntity> result = currencyRepository.findById(currency.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo(currency.getCode());
        assertThat(result.get().getExchangeRate().compareTo(currency.getExchangeRate()) == 0);
    }

    @Test
    public void testUpdateAndDeleteCurrency() {
        CurrencyEntity currency = TestData.CreateTestCurrencyEntity1();
        currencyRepository.save(currency);
        Integer id = currency.getId();

        currency.setExchangeRate(BigDecimal.valueOf(1.53));
        currencyRepository.save(currency);

        Optional<CurrencyEntity> updated = currencyRepository.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get().getExchangeRate().compareTo(BigDecimal.valueOf(1.53)) == 0);

        currencyRepository.deleteById(id);

        Optional<CurrencyEntity> deleted = currencyRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}