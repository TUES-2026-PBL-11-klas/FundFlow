package com.Edu_App.repositories;

import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.TransferEntity;

public interface TransferRepository extends CrudRepository<TransferEntity, Integer>{
    
}
