package com.Edu_App.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;

import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.domain.entities.UserStatus;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.repositories.UserRepository;
import com.Edu_App.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userR)
    {
        this.userRepository = userR;
    }

    @Override
    public UserEntity createUser(UserEntity userE) throws BadRequestException
    {
        Optional<UserEntity> userWithThatUsername = this.userRepository.findByUsername(userE.getUsername());
        if(userWithThatUsername.isPresent() && userWithThatUsername.get().getStatus().equals(UserStatus.ACTIVE))
        {
            throw new BadRequestException("this username is already taken");
        }
        Optional<UserEntity> userWithThatEmail = this.userRepository.findByEmail(userE.getEmail());
        if(userWithThatEmail.isPresent() && userWithThatEmail.get().getStatus().equals(UserStatus.ACTIVE))
        {
            throw new BadRequestException("this email is already taken");
        }
        return this.userRepository.save(userE);
    }
    
    @Override
    public UserEntity findUserById(Integer id) throws ResourceNotFoundException
    {
        Optional<UserEntity> user = this.userRepository.findById(id);
        if(!user.isPresent())
        {
            throw new ResourceNotFoundException("this user does not exist");
        }
        return user.get();
    }
    @Override
    public UserEntity findActiveUserById(Integer id) throws ResourceNotFoundException
    {
        Optional<UserEntity> user = this.userRepository.findById(id);
        if(!user.isPresent() || !user.get().getStatus().equals(UserStatus.ACTIVE))
        {
            throw new ResourceNotFoundException("this user does not exist");
        }
        return user.get();
    }

    @Override
    public UserEntity findUserByUsername(String username)
    {
        Optional<UserEntity> user = this.userRepository.findByUsername(username);
        if(!user.isPresent() || !user.get().getStatus().equals(UserStatus.ACTIVE))
        {
            throw new ResourceNotFoundException("user with this username does not exist");
        }
        return user.get();
    }

    @Override
    public UserEntity findUserByEmail(String email)
    {
        Optional<UserEntity> user = this.userRepository.findByEmail(email);
        if(!user.isPresent() || !user.get().getStatus().equals(UserStatus.ACTIVE))
        {
            throw new ResourceNotFoundException("user with this email does not exist");
        }
        return user.get();
    }

    @Override
    public List<UserEntity> getAllUsers()
    {
        Iterable<UserEntity> users = this.userRepository.findAll();
        List<UserEntity> userList = new ArrayList<>();
        users.forEach(userList::add);
        return userList;
    }

    @Override
    public List<UserEntity> getAllActiveUsers()
    {
        return userRepository.findAllByStatus(UserStatus.ACTIVE);
    }
    
    @Override
    public UserEntity updateUser(Integer id, UserEntity newUser) throws BadRequestException
    {
        UserEntity user = findActiveUserById(id);
        Optional<UserEntity> existingUserWithThisUsername = this.userRepository.findByUsername(newUser.getUsername());
        if(existingUserWithThisUsername.isPresent() && !existingUserWithThisUsername.get().getId().equals(id))
        {
            throw new BadRequestException("this username is already taken");
        }
        user.setUsername(newUser.getUsername());
        Optional<UserEntity> existingUserWithThisEmail = this.userRepository.findByEmail(newUser.getEmail());
        if(existingUserWithThisEmail.isPresent() && !existingUserWithThisEmail.get().getId().equals(id))
        {
            throw new BadRequestException("this email is already taken");
        }
        user.setEmail(newUser.getEmail());
        user.setHashPassword(newUser.getHashPassword());
        return this.userRepository.save(user);
    }

    @Override
    public void deleteUserById(Integer id)
    {
        UserEntity user = findUserById(id);
        user.setStatus(UserStatus.DELETED); 
        userRepository.save(user);
    }
    @Override
    public void updateStatus(Integer id, UserStatus status)
    {
        if(status.equals(UserStatus.DELETED))
        {
            this.deleteUserById(id);
            return;
        }
        if(status.equals(UserStatus.ACTIVE))
        {
            UserEntity inactiveUser = this.findUserById(id);
            if(this.userRepository.findByUsernameAndStatus(inactiveUser.getUsername(), UserStatus.ACTIVE).isPresent())
            {
                throw new BadRequestException("User with that username is active");
            }
            if(this.userRepository.findByEmailAndStatus(inactiveUser.getEmail(), UserStatus.ACTIVE).isPresent())
            {
                throw new BadRequestException("User with that email is active");
            }
            inactiveUser.setStatus(status);
            userRepository.save(inactiveUser);
        }
    }
}
