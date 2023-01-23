package com.phaseb.customer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phaseb.customer.model.Customer;

@Repository
public interface CustomerDAO extends JpaRepository<Customer, Long> {
	
	
	public Customer findById(long id); 
	public Customer findByEmail(String email);
	public Customer findByIdAndEmail(long id, String email);
	public Customer findByEmailAndPassword(String email, String password);
}
