package com.phaseb.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.phaseb.gateway.filter.JwtAuthenticationFilter;


@Configuration
public class GatewayConfig {

	@Autowired
	private JwtAuthenticationFilter filter;

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("auth", r -> r.path("/auth/**").filters(f -> f.filter(filter)).uri("lb://auth"))
				.route("customer-service", r -> r.path("/api/customers/**").filters(f -> f.filter(filter)).uri("lb://customer-service"))
				.route("company-service", r -> r.path("/api/companies/**").filters(f -> f.filter(filter)).uri("lb://company-service"))
				.route("coupon-service", r -> r.path("/api/coupons/**").filters(f -> f.filter(filter)).uri("lb://coupon-service")).build();
	}

}