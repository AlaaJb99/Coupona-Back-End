package com.phaseb.auth.filter;

import java.awt.print.Printable;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	// because we are gonna calling the authentication manager to authenticate the
	// user so we are gonna inject it in this class
	private final AuthenticationManager authenticationManager;
	
	private String jwtSecret = "BvPHGM8C0ia4uOuxxqPD5DTbWC9F9TWvPStp3pb7ARo0oK2mJ3pd3YG4lxA9i8bj6OTbadwezxgeEByY";

	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;

	}

	// now we need to work on the ethic authentication,
	// So we need to call the authentication manager,
	// passing the user credentials, and then Let's spring to it's magic.
	// and then whenever the login is successful, in which case the
	// successfulAuthentication () will be called
	// then we had to send in the access token and the refresh token to the user and
	// the headers or and the response or something
	// So first, we will work on the attempt authentication
	// removing return super.attemptAuthentication(request, response);

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		String email;// = request.getParameter("email");
		String password;// = request.getParameter("password");

		try {
			Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
			email = requestMap.get("email");
			password = requestMap.get("password");
		} catch (IOException e) {
			throw new AuthenticationServiceException(e.getMessage(), e);
		}

		log.info("Email is: {}", email);
		log.info("Password is: {}", password);
		// create Object of email password authentication token
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
				password);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		// here we are gonna pass the token to the user
		// DETECTING THE USER WHO successfully LOGGED IN
		User user = (User) authentication.getPrincipal();// here we can get the user who successfully authenticated
		// now we can grab the information of the user to create the JWT
		// selecting the cryptography algorithm we gonna use to sign the JSON web token
		// and the refresh token.
		Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
		// create the access token
		String access_token = JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles",
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		Collection<GrantedAuthority> claims = user.getAuthorities();
		log.info(user.getUsername());
		// here using the response to send them to the user		
		Map<String, String> tokensMap = new HashMap<>();
		tokensMap.put("token", access_token);
		tokensMap.put("roles", claims.toArray()[0].toString());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokensMap);
	}
}
