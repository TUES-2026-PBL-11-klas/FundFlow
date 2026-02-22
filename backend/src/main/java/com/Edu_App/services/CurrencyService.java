package com.Edu_App.services;

import java.util.List;

import com.Edu_App.domain.entities.CurrencyEntity;

public interface CurrencyService {
    public CurrencyEntity createCurrency(CurrencyEntity currencyEntity);
    public CurrencyEntity findCurrencyById(Integer id);
    public CurrencyEntity findCurrencyByCode(String code);
    public List<CurrencyEntity> getAllCurrencies();
    public void updateCurrencyCode(Integer id, String newCode);
    public void updateCurrencyExchangeRate(Integer id, double newExchangeRate);
    
}
