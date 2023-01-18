package com.phaseb.company.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.phaseb.company.model.Company;

public interface CompanyDAO extends JpaRepository<Company, Integer> {
	
	public Company findByEmailAndPassword(String email, String password);
	public Company findById(int id);
	public Company findByName(String name);
	public Company findByEmail(String email);
	public Company findByNameOrEmail(String name, String email);
}
