package com.Edu_App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.Edu_App.domain.entities.UserEntity;
import com.Edu_App.repositories.UserRepository;

@SpringBootApplication
public class EduAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduAppApplication.class, args);
	}

	@Bean
CommandLineRunner run(UserRepository userRepository) {
    return args -> {
        UserEntity testUser = UserEntity.builder()
                .username("Ivan_Ivanov")
                .email("ivan@example.com")
                .hashPassword("secret123")
                .build();

        userRepository.save(testUser);
        System.out.println("save a rec to pgAdmin.");
    };

}
}


