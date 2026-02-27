package com.Edu_App.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Edu_App.TestData;
import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.domain.entities.UserStatus;
import com.Edu_App.exceptions.BadRequestException;
import com.Edu_App.exceptions.ResourceNotFoundException;
import com.Edu_App.services.UserService;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserServiceTests 
{

    @Autowired
    private UserService userService;



    @Test
    public void testCreateUserAndFindById() {
        UserEntity user = TestData.CreateTestUserEntity1();
        UserEntity savedUser = this.userService.createUser(user);

        UserEntity foundUser = this.userService.findUserById(savedUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void createUserThrowingException()
    {
        UserEntity user1 = TestData.CreateTestUserEntity1();
        UserEntity user2 = TestData.CreateTestUserEntity1();
        this.userService.createUser(user1);
        assertThrows(BadRequestException.class, () -> {this.userService.createUser(user2);});
        user2.setUsername("new Username");
        assertThrows(BadRequestException.class, () -> {this.userService.createUser(user2);});
        user2.setEmail("new Email");
        assertDoesNotThrow(() -> {this.userService.createUser(user2);});
        
    }
    @Test
    public void testFindUserByIdThrowsNotFound() 
    {
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(999));
    }

    @Test
    public void testGetAllUsers() {
        userService.createUser(TestData.CreateTestUserEntity1());
        UserEntity user2 = TestData.CreateTestUserEntity2(); 
        userService.createUser(user2);
        List<UserEntity> allUsers = userService.getAllUsers();
        assertThat(allUsers).isNotEmpty();
        assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    public void testUpdateUser() {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        
        UserEntity updateDetails = new UserEntity();
        updateDetails.setUsername("updated_name");
        updateDetails.setEmail("updated@email.com");
        updateDetails.setHashPassword("new_pass");

        UserEntity updatedUser = userService.updateUser(user.getId(), updateDetails);

        assertThat(updatedUser.getUsername()).isEqualTo("updated_name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    public void testUpdateUserThrowException()
    {
        UserEntity user1 = userService.createUser(TestData.CreateTestUserEntity1());
        this.userService.createUser(TestData.CreateTestUserEntity2());
        UserEntity updateDetails = TestData.CreateTestUserEntity2();
        assertThrows(BadRequestException.class, () -> {this.userService.updateUser(user1.getId(), updateDetails);});
    }

    @Test
    public void testDeleteUser() 
    {
        UserEntity user = userService.createUser(TestData.CreateTestUserEntity1());
        Integer id = user.getId();

        userService.deleteUserById(id);

        assertThrows(ResourceNotFoundException.class, () -> userService.findActiveUserById(id));
        assertDoesNotThrow(() -> userService.findUserById(id));
    }

    @Test
    public void testDeleteUserThrowException()
    {
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(999));
    }

    @Test 
    public void testFindActiveUserThrowException()
    {
        UserEntity user1 = userService.createUser(TestData.CreateTestUserEntity1());
        this.userService.deleteUserById(user1.getId());
        assertThrows(ResourceNotFoundException.class, () -> userService.findActiveUserById(user1.getId()));
        UserEntity user2 = TestData.CreateTestUserEntity1();
        assertDoesNotThrow(() -> {this.userService.createUser(user2);});
    }
    
    @Test
    public void testUpdateStatusThrowException() 
    {
        UserEntity user1 = userService.createUser(TestData.CreateTestUserEntity1());
        userService.updateStatus(user1.getId(), UserStatus.DELETED);
        UserEntity findDeletedUser = this.userService.findUserById(user1.getId());
        assertThat(findDeletedUser.getStatus()).isEqualTo(UserStatus.DELETED);
        UserEntity user2 = TestData.CreateTestUserEntity2();
        user2.setEmail(user1.getEmail());
        userService.createUser(user2);

        assertThrows(BadRequestException.class, () -> 
            userService.updateStatus(user1.getId(), UserStatus.ACTIVE));
    }

}
