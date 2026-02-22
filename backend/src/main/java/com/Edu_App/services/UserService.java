package com.Edu_App.services;

import java.util.List;


import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.domain.entities.UserStatus;

public interface UserService {
    
    public UserEntity createUser(UserEntity userE);
    public UserEntity findUserById(Integer id);
    public UserEntity findUserByUsername(String userName);
    public UserEntity findUserByEmail(String email);
    public List<UserEntity> getAllUsers();
    public UserEntity updateUser(Integer id, UserEntity newUser);
    public void deleteUserById(Integer id);
    public UserEntity findActiveUserById(Integer id);
    public List<UserEntity> getAllActiveUsers();
    public void updateStatus(Integer id, UserStatus status);
}
