package com.Edu_App.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.CurrencyEntity;

public interface CurrencyRepository extends CrudRepository<CurrencyEntity, Integer>{
    

    public Optional<CurrencyEntity> findByCode(String code);

}
