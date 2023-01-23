package com.phaseb.company.controller;

import java.net.URISyntaxException;

import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phaseb.company.model.Company;
import com.phaseb.company.service.CompanyService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/companies") // https://localhost:8080/api/companies
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	// Writing the CRUD Operation (GET, POST, PUT, DELETE)

	// this GET Request used for two purposes 1 to login to check if it exists 2 to
	// get the details of the company

	/* this will return all the existing companies */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin")
	public Iterable<Company> getCompanies() {
		return companyService.getAllCompanies();
	}

//	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY')")
//	@GetMapping("/company")
//	public Company getCompany(@RequestBody CompanyId id) {
//		return companyService.getOneCompany(id.getId());
//	}

	@PreAuthorize("hasAnyAuthority('CUSTOMER')")
	@GetMapping("/customer")
	public Company getCompanNameyById(@RequestParam("companyId") int companyId) {
		log.info("get Company Name " + companyId);
		return companyService.getOneCompany(companyId);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY')")
	@GetMapping("/company")
	public Company getCompanyDetails(@RequestParam("email") String email, @RequestParam("password") String password) {
		return companyService.getCompanyDetails(email, password);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin")
	public boolean addCompany(@RequestBody Company company) throws URISyntaxException {
		return companyService.addCompany(company);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/admin")
	public boolean updateCompany(@RequestBody Company company) {
		log.info(" in update conroller");
		return companyService.updateCompany(company);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/admin")
	public boolean deleteCompany(@RequestBody Company company) {
		return companyService.deleteCompany(company);
		// when return need to delete the company coupons
	}

}

@Data
class CompanyDetails {
	private String email;
	private String password;
}
