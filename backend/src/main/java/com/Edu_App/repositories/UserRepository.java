package com.Edu_App.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer>{

    public Optional<UserEntity> findByUsername(String username);
}
