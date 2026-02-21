package com.Edu_App.repositoryTests;



import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.repositories.UserRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTests {
    
    private UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(UserRepository userR)
    {
        this.userRepository = userR;
    }

    @Test
    public void testSaveAndFindUser() {
        UserEntity user = TestData.CreateTestUserEntity1();
        this.userRepository.save(user);
        Optional<UserEntity> result = this.userRepository.findById(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
        
    }

    @Test
    public void testUpdateAndDeleteUser()
    {
        UserEntity user = TestData.CreateTestUserEntity1();
        user = this.userRepository.save(user);
        Integer userId = user.getId();
        user.setUsername("Updated Name");
        user.setEmail("updated@gmail.com");
        this.userRepository.save(user);
        Optional<UserEntity> result1 = this.userRepository.findById(userId);
        assertThat(result1).isPresent();
        assertThat(result1.get().getUsername()).isEqualTo("Updated Name");

        this.userRepository.deleteById(userId);
        Optional<UserEntity> result2 = this.userRepository.findById(userId);
        assertThat(result2).isEmpty();
    }
}
