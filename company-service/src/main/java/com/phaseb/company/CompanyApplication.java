package com.phaseb.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.phaseb.company.controller.CompanyController;
import com.phaseb.company.dao.CompanyDAO;
import com.phaseb.company.model.Company;
import com.phaseb.company.service.CompanyService;

@SpringBootApplication
public class CompanyApplication {
	
//	@Autowired
//	CompanyController companyController;
//	@Autowired
//	CompanyDAO companyDAO;

	public static void main(String[] args) {
		SpringApplication.run(CompanyApplication.class, args);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
	
	@Bean
	CommandLineRunner runner(CompanyService companyService){
		return args->{
			//companyService.addCompany(new Company(1, "SHEI", "service@shen.com", "123"));
//			companyService.addCompany(new Company(2, "Pizza Hut", "jane.doe@pizzahut.com", "123"));
//			companyService.addCompany(new Company(3, "Mahsanei Hashmal", "cs.mh@pyango.co.il", "123"));
//			companyService.addCompany(new Company(4, "Rosenfeld", "contactus@ikea.co.il", "123"));
			//companyDAO.save(new Company(3000, "Pizza Hut", "pizza.hut@gmail.com", "1234567890"));
			//companyController.getCompanies();
//			couponRepository.save(new Coupon(0, 3000, Category.FOOD, "10% on Pizza Free Gluten", 
//					"Get 10% discount on Pizza free gluten", startDate, endDate, 100, 20.0));
		};
	}

}
