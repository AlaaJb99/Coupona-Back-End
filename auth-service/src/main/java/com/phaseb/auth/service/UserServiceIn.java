package com.phaseb.auth.service;

import java.util.List;

import com.phaseb.auth.model.User;

public interface UserServiceIn {

	public User addUser(User user);
	public void addUserRole(String email, String roleName);
	public User getUser(String email);
	public List<User> getUsers();//just get first page to not get too heaVY on opur backend
	void deleteUser(User user);
	User updateUser(String oldEmail, String ewEmail, String password);
}
