package com.Edu_App.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.services.CurrencyService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CurrencyServiceTests {

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void testCreateCurrencyAndFindById() {
        CurrencyEntity currency = TestData.CreateTestCurrencyEntity1();
        CurrencyEntity saved = currencyService.createCurrency(currency);

        CurrencyEntity found = currencyService.findCurrencyById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getCode()).isEqualTo(currency.getCode());
    }

    @Test
    public void testCreateCurrencyThrowsException() {
        CurrencyEntity currency1 = TestData.CreateTestCurrencyEntity1();
        CurrencyEntity currency2 = TestData.CreateTestCurrencyEntity1();

        currencyService.createCurrency(currency1);

        assertThrows(BadRequestException.class, () -> {
            currencyService.createCurrency(currency2);
        });

        currency2.setCode("NEW_CODE");

        assertDoesNotThrow(() -> {
            currencyService.createCurrency(currency2);
        });
    }

    @Test
    public void testFindCurrencyByIdThrowsException() {
        assertThrows(ResourceNotFoundException.class, () ->
                currencyService.findCurrencyById(999));
    }

    @Test
    public void testFindCurrencyByCodeThrowsException() {
        assertThrows(ResourceNotFoundException.class, () ->
                currencyService.findCurrencyByCode("INVALID"));
    }

    @Test
    public void testGetAllCurrencies() {
        currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());
        currencyService.createCurrency(TestData.CreateTestCurrencyEntity2());

        List<CurrencyEntity> all = currencyService.getAllCurrencies();

        assertThat(all).isNotEmpty();
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    public void testUpdateCurrencyCode() {
        CurrencyEntity currency =
                currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        currencyService.updateCurrencyCode(currency.getId(), "UPDATED");

        CurrencyEntity updated =
                currencyService.findCurrencyById(currency.getId());

        assertThat(updated.getCode()).isEqualTo("UPDATED");
    }

    @Test
    public void testUpdateCurrencyCodeThrowsException() {
        CurrencyEntity currency1 =
                currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        CurrencyEntity currency2 =
                currencyService.createCurrency(TestData.CreateTestCurrencyEntity2());

        assertThrows(BadRequestException.class, () ->
                currencyService.updateCurrencyCode(
                        currency1.getId(),
                        currency2.getCode()
                )
        );
    }

    @Test
    public void testUpdateExchangeRate() {
        CurrencyEntity currency =
                currencyService.createCurrency(TestData.CreateTestCurrencyEntity1());

        currencyService.updateCurrencyExchangeRate(currency.getId(), 2.5);

        CurrencyEntity updated =
                currencyService.findCurrencyById(currency.getId());

        assertThat(updated.getExchangeRate()).isEqualTo(2.5);
    }

    @Test
    public void testUpdateExchangeRateThrowsException() {
        assertThrows(ResourceNotFoundException.class, () ->
                currencyService.updateCurrencyExchangeRate(999, 3.0));
    }
}