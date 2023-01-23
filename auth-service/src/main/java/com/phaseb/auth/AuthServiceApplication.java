package com.phaseb.auth;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.phaseb.auth.model.Role;
import com.phaseb.auth.model.User;
import com.phaseb.auth.service.UserService;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			
			Role admin = new Role(1,"ADMIN");
			Role company = new Role(2,"COMPANY");
			Role customer = new Role(3,"CUSTOMER");
			userService.addRole(admin);
			userService.addRole(company);
			userService.addRole(customer);
			userService.addUser(new User(null, "admin@admin.com", "123", Arrays.asList(admin)));
//			userService.addUser(new User(null, "ibra", "123", new ArrayList<>()));
//			userService.addUser(new User(null, "jojo", "123", new ArrayList<>()));

		};
	}
}
