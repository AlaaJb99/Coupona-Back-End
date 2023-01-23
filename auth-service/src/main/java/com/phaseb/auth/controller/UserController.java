package com.phaseb.auth.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.phaseb.auth.model.User;
import com.phaseb.auth.service.UserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
	
	@Autowired
	private UserService userService;
	
//	@GetMapping("/users")
//	public ResponseEntity<List<User>> getUsers(){
//		return ResponseEntity.ok().body(userService.getUsers());
//	}
	
	//signup
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/register")
	public ResponseEntity<User> addUser(@RequestBody User user){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/auth/register").toUriString());
		return ResponseEntity.created(uri).body(userService.addUser(user));
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/addRoleToUser")
	public ResponseEntity<String> addUserRole(@RequestBody String  email, String role){
		userService.addUserRole(email, role);
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/delete")
	public void deleteUser(@RequestBody User user){
		log.info("delete user controller");
		userService.deleteUser(user);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/update")
	public void updateUser(@RequestBody UpdateUser user){
		log.info("edit user controller ");
		userService.updateUser(user.getOldEmail(), user.getEmail(), user.getPassword());
	}
	

}

@Data
class UpdateUser{
	String oldEmail;
	String email;
	String password;
}
