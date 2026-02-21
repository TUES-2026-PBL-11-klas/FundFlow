package com.Edu_App.services;

import java.util.List;


import com.Edu_App.domain.entities.UserEntity;

public interface UserService {
    
    public UserEntity createUser(UserEntity userE);
    public UserEntity findUserById(Integer id);
    public UserEntity findUserByUsername(String userName);
    public UserEntity findUserByEmail(String email);
    public List<UserEntity> getAllUsers();
    public UserEntity updateUser(Integer id, UserEntity newUser);
    public void deleteUserById(Integer id);
}
