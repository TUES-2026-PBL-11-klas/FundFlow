package com.Edu_App.repositories;

import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.AccountEntity;

public interface AccountRepository extends CrudRepository<AccountEntity, String>{

}
