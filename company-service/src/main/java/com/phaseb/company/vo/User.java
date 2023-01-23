package com.phaseb.company.vo;

import java.util.ArrayList;
import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private String email;
	private String password;
	/* Many users belong to one Role Type */
	private Collection<Role> roles = new ArrayList<>();
}
