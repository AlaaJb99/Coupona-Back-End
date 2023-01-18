package com.phaseb.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phaseb.auth.model.User;

public interface UserRepo extends JpaRepository<User, Long>{
	
	public User findByEmail(String email);
}
