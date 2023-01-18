package com.phaseb.company.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.phaseb.company.dao.CompanyDAO;
import com.phaseb.company.model.Company;
import com.phaseb.company.vo.Role;
import com.phaseb.company.vo.User;

@Service
public class CompanyService {

	@Autowired
	CompanyDAO companyDAO;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	HttpServletRequest request;

	public Company getCompanyDetails(String email, String password) {
		Company company = companyDAO.findByEmailAndPassword(email, password);
		if (company != null)
			return company;
		return null;
	}

	public boolean isCompanyExists(String email, String password) {
		Company company = companyDAO.findByEmailAndPassword(email, password);
		if (company != null)
			return true;
		return false;
	}

	public boolean addCompany(Company newCompany) {
		Company company = companyDAO.findByNameOrEmail(newCompany.getName(), newCompany.getEmail());

		if (company != null)// there is a company with this id or this name or this email
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name Or email Exists");

		updateInAuthService(newCompany, "register");

		// encrypt the password before saving the company in the database
		newCompany.setPassword(newCompany.getPassword());
		companyDAO.save(newCompany);

		return true;
	}

	public boolean updateCompany(Company company) {
		Company updateCompany = companyDAO.findByName(company.getName());// because the name conn't be changed
		if (updateCompany == null) // company not exists
			return false;

		// wants to change the email
		if (updateCompany.getEmail().compareTo(company.getEmail()) != 0) {
			updateCompany = companyDAO.findByEmail(company.getEmail());
			if (updateCompany != null)// there are a company with this email
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id Or email Exists");
		}
		// send the old email with
		String url = "http://AUTH/auth/update";
		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(request.getHeader("Authorization").substring("Bearer ".length()));
		// request body parameters
		Map<String, Object> map = new HashMap<>();
		// the old email
		map.put("oldEmail", updateCompany.getEmail());
		// the new email
		map.put("email", company.getEmail());
		map.put("password", company.getPassword());

		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		// execute the request
		restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);

		updateCompany.setEmail(company.getEmail());
		// encrypt the password before saving the company in the database
		updateCompany.setPassword(company.getPassword());

		companyDAO.save(updateCompany);
		return true;
	}

	public boolean deleteCompany(Company company) {
		if (isCompanyExists(company.getEmail(), company.getPassword())) {
			Company deleteCompany = companyDAO.findByEmail(company.getEmail());
			// delete the company coupons
			String url = "http://COUPON-SERVICE/api/coupons/delete";
			HttpHeaders headers = new HttpHeaders();
			// set the content type and the authorization token
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(request.getHeader("Authorization").substring("Bearer ".length()));
			Map<String, Object> map = new HashMap<>();
			// set the body parameters
			map.put("id", deleteCompany.getId());
			// build the request
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
			restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			// send a delete request in the authentication service
			updateInAuthService(company, "delete");

			companyDAO.delete(companyDAO.findByEmail(company.getEmail()));
			return true;
		}
		return false;
	}

	public Iterable<Company> getAllCompanies() {
		return companyDAO.findAll();
	}

	public Company getOneCompany(int id) {
		return companyDAO.findById(id);
	}

	private void updateInAuthService(Company company, String req) {
		// add it in the users database with it's role
		String url = "http://AUTH/auth/" + req;
		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(request.getHeader("Authorization").substring("Bearer ".length()));
		// request body parameters
		Map<String, Object> map = new HashMap<>();
		map.put("email", company.getEmail());
		map.put("password", company.getPassword());
		map.put("roles", Arrays.asList(new Role(2, "COMPANY")));
		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		if (req.compareTo("register") == 0)
			// send POST request
			restTemplate.postForEntity(url, entity, String.class);
		if (req.compareTo("delete") == 0)
			restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

	}

}
