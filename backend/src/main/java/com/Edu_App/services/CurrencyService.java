package com.Edu_App.services;

import java.math.BigDecimal;
import java.util.List;

import com.Edu_App.domain.entities.CurrencyEntity;

public interface CurrencyService {
    public CurrencyEntity createCurrency(CurrencyEntity currencyEntity);
    public CurrencyEntity findCurrencyById(Integer id);
    public CurrencyEntity findCurrencyByCode(String code);
    public List<CurrencyEntity> getAllCurrencies();
    public void updateCurrencyCode(Integer id, String newCode);
    public void updateCurrencyExchangeRate(Integer id, BigDecimal newExchangeRate);
    public BigDecimal convertAmount(BigDecimal amount, Integer fromId, Integer toId);
}
