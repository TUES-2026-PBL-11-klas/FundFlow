package com.Edu_App.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.AccountEntity;
import com.Edu_App.domain.entities.AccountStatus;
import com.Edu_App.domain.entities.UserEntity;

public interface AccountRepository extends CrudRepository<AccountEntity, Integer>{
    public Optional<AccountEntity> findByIban(String iban);

    public List<AccountEntity> findAllByOwner(UserEntity owner);

    public Optional<AccountEntity> findByIbanAndStatus(String iban, AccountStatus status);

    public List<AccountEntity> findAllByOwnerAndStatus(UserEntity owner, AccountStatus status);

    public List<AccountEntity> findAllByStatus(AccountStatus status);
}
