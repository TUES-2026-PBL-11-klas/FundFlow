package com.Edu_App.repositories;

import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.CurrencyEntity;

public interface CurrencyRepository extends CrudRepository<CurrencyEntity, Integer>{
    
}
