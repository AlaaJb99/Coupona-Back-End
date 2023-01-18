package com.phaseb.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phaseb.auth.model.Role;

public interface RoleRepo extends JpaRepository<Role, Integer> {
	
	public Role findByName(String name);
}
