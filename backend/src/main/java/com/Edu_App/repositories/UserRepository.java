package com.Edu_App.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.domain.entities.UserStatus;

public interface UserRepository extends CrudRepository<UserEntity, Integer>{
    public Optional<UserEntity> findByUsername(String username);
    public Optional<UserEntity> findByEmail(String email);
    
    public Optional<UserEntity> findByUsernameAndStatus(String username, UserStatus status);

    public Optional<UserEntity> findByEmailAndStatus(String email, UserStatus status);

    public List<UserEntity> findAllByStatus(UserStatus status);
}
