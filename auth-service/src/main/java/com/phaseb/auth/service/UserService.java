package com.phaseb.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phaseb.auth.model.Role;
import com.phaseb.auth.model.User;
import com.phaseb.auth.repository.RoleRepo;
import com.phaseb.auth.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserServiceIn, UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// Logic to get the user from the DB
		log.info(username);
		User user = userRepo.findByEmail(username);
		if (user == null) {
			log.error("user not found in the database");
			throw new UsernameNotFoundException("user not found in the database");
		} else {
			log.info("user found in the database {}: ", username);
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}

	@Override
	public User addUser(User user) {
		if (userRepo.findByEmail(user.getEmail()) != null)
			return null;
		// Encoding the password before saving it in the Database
		log.info(user.getRoles().toString());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public void addUserRole(String email, String roleName) {
		User user = userRepo.findByEmail(email);
		Role role = roleRepo.findByName(roleName);
		// user.getRole().add(new Role(null, role));
		// userRepo.save(user);
		user.getRoles().add(role);// because of the transactional it saves it in the other table
		// return role;
	}

	@Override
	public void deleteUser(User user) {
		User deleteUser = userRepo.findByEmail(user.getEmail());
		userRepo.delete(deleteUser);
	}

	@Override
	public User updateUser(String oldEmail, String newEmail, String password) {
		User user = userRepo.findByEmail(oldEmail);
		user.setEmail(newEmail);
		user.setPassword(passwordEncoder.encode(password));
		return userRepo.save(user);
	}

	@Override
	public User getUser(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public List<User> getUsers() {
		return userRepo.findAll();
	}

	public Role addRole(Role role) {
		return roleRepo.save(role);
	}

}
