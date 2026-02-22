package com.Edu_App.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Edu_App.domain.entities.CurrencyEntity;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.repositories.CurrencyRepository;
import com.Edu_App.services.CurrencyService;

@Service
public class CurrencyServiceImpl implements CurrencyService {
    private CurrencyRepository currencyRepository;

    public CurrencyServiceImpl(CurrencyRepository currencyR)
    {
        this.currencyRepository = currencyR;
    }

    @Override
    public CurrencyEntity createCurrency(CurrencyEntity currencyEntity)
    {
        Optional<CurrencyEntity> currencyWithThisCode = this.currencyRepository.findByCode(currencyEntity.getCode());   
        if(currencyWithThisCode.isPresent())
        {
            throw new BadRequestException("Currency with this code already exist");
        }
        return this.currencyRepository.save(currencyEntity);
    }   
    @Override
    public CurrencyEntity findCurrencyById(Integer id)
    {
        Optional<CurrencyEntity> currency = this.currencyRepository.findById(id);
        if(!currency.isPresent())
        {
            throw new ResourceNotFoundException("this currency does not exist");
        }
        return currency.get();
    }
    @Override
    public CurrencyEntity findCurrencyByCode(String code)
    {
        Optional<CurrencyEntity> currency = this.currencyRepository.findByCode(code);
        if(!currency.isPresent())
        {
            throw new ResourceNotFoundException("this currency does not exist");
        }
        return currency.get();
    }
    @Override
    public List<CurrencyEntity> getAllCurrencies()
    {
        Iterable<CurrencyEntity> currencies =  this.currencyRepository.findAll();
        List<CurrencyEntity> currencyList = new ArrayList<>();
        currencies.forEach(currencyList::add);
        return currencyList;
    }
 

    public void updateCurrencyCode(Integer id, String newCode)
    {
        Optional<CurrencyEntity> currency = this.currencyRepository.findById(id);
        if(!currency.isPresent())
        {
            throw new ResourceNotFoundException("this currency does not exist");
        }
        Optional<CurrencyEntity> currencyWithThisCode = this.currencyRepository.findByCode(newCode);
        if(currencyWithThisCode.isPresent() && !currencyWithThisCode.get().getId().equals(id))
        {
            throw new BadRequestException("Currency with this code already exist");
        }
        currency.get().setCode(newCode);
        this.currencyRepository.save(currency.get());
        
    }

    public void updateCurrencyExchangeRate(Integer id, double newExchangeRate)
    {
        Optional<CurrencyEntity> currency = this.currencyRepository.findById(id);
        if(!currency.isPresent())
        {
            throw new ResourceNotFoundException("this currency does not exist");
        }
        currency.get().setExchangeRate(newExchangeRate);
        this.currencyRepository.save(currency.get());
    }




}
