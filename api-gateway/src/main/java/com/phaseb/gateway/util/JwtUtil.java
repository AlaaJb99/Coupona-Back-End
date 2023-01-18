package com.phaseb.gateway.util;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.phaseb.gateway.exception.JwtTokenMalformedException;
import com.phaseb.gateway.exception.JwtTokenMissingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	// @Value("${jwt.secret}")
	private String jwtSecret = "BvPHGM8C0ia4uOuxxqPD5DTbWC9F9TWvPStp3pb7ARo0oK2mJ3pd3YG4lxA9i8bj6OTbadwezxgeEByY";

	public String[] getClaims(final String token) {
		try {
			String tokenV = token.substring("Bearer ".length());

			Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(tokenV);
			String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
			return roles;
		} catch (Exception e) {
			System.out.println(e.getMessage() + " => " + e);
		}
		return null;
	}

	public void validateToken(final String token)
			throws JwtTokenMalformedException, JwtTokenMissingException, TokenExpiredException {
		// Verify the token (decode the encrypted token to get the roles
		String tokenV = token.substring("Bearer ".length());
		Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT decodedJWT = verifier.verify(tokenV);
		// String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

	}

}