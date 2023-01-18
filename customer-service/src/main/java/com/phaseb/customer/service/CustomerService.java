package com.phaseb.customer.service;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.phaseb.customer.dao.CustomerDAO;
import com.phaseb.customer.model.Customer;
import com.phaseb.customer.vo.ResponseTemplateVO;
import com.phaseb.customer.vo.Role;
import com.phaseb.customer.vo.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

	@Autowired
	private CustomerDAO customerDAO;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	HttpServletRequest request;

	private boolean isExists(Customer customer) {
		Customer customerExists = customerDAO.findById(customer.getId());
		if (customerExists != null)
			return true;
		customerExists = customerDAO.findByEmail(customer.getEmail());
		if (customerExists != null)
			return true;
		return false;
	}

	public Customer addCustomer(Customer customer) {

		if (isExists(customer)) {
			log.info("Exists");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id Or email Exists");
		}

		// encrypt the password before adding to database
		customer.setPassword(customer.getPassword());

		// add him also to the authentication service
		updateInAuthService(customer, "register");

		return customerDAO.save(customer);
	}

	public Iterable<Customer> getAllCustomers() {
		return customerDAO.findAll();
	}

	public Customer updateCustomer(Customer customer) {

		Customer updateCustomer = customerDAO.findById(customer.getId());
		if (updateCustomer == null)
			return null;
		// wants to change the email check if there is another user with this new email
		if (updateCustomer.getEmail().compareTo(customer.getEmail()) != 0) {
			Customer newEmailCustomer = customerDAO.findByEmail(customer.getEmail());
			//there an existing email for this email
			if (newEmailCustomer != null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email exists");
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
		map.put("oldEmail", updateCustomer.getEmail());
		// the new email
		map.put("email", customer.getEmail());
		map.put("password", customer.getPassword());

		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		// execute the request
		restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);

		updateCustomer.setFirstName(customer.getFirstName());
		updateCustomer.setLastName(customer.getLastName());
		updateCustomer.setEmail(customer.getEmail());
		updateCustomer.setPassword(passwordEncoder.encode(customer.getPassword()));
		return customerDAO.save(customer);
	}

	public void deleteCustomer(Customer customer) {
		updateInAuthService(customer, "delete");
		// delete all the purchased history
		String url = "http://coupon-service/api/coupons/deleteAll/purchased";
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(request.getHeader("Authorization").substring("Bearer ".length()));
		// request body parameters
		Map<String, Object> map = new HashMap<>();
		map.put("customerId", customer.getId());
		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
		// make http delete request
		restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
		customerDAO.delete(customer);
	}

	public ResponseTemplateVO getCustomerWihCoupons(long id) throws RestClientException {
		ResponseTemplateVO vo = new ResponseTemplateVO();
		Customer customer = customerDAO.findById(id);
		// here we want the coupons of the we need
		// to go to the coupon service to request the coupons for this customer

		// we are gonna use synchronous communication because we want to send a request
		// and wait to get it
		// here we want to fetch the Customer Coupons (the customer coupons are saved in
		// the Coupon Service Database)

		ResponseEntity<ResponseTemplateVO> responseEntity = restTemplate.getForEntity(
				"http://COUPON-SERVICE/api/coupons/purchase/" + customer.getId(), ResponseTemplateVO.class);

		ResponseTemplateVO coupons = responseEntity.getBody();
		vo.setCoupon(coupons.getCoupon());
		vo.setCustomerId(customer.getId());
		return vo;
	}

	public Customer getCustomer(String email) {
		return customerDAO.findByEmail(email);
	}

	private void updateInAuthService(Customer customer, String req) {
		// add it in the users database with it's role
		String url = "http://auth/auth/" + req;
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(request.getHeader("Authorization").substring("Bearer ".length()));

		// request body parameters
		Map<String, Object> map = new HashMap<>();
		map.put("email", customer.getEmail());
		map.put("password", customer.getPassword());
		map.put("roles", Arrays.asList(new Role(3, "CUSTOMER")));
		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		if (req.compareTo("register") == 0)
			// send POST request
			restTemplate.postForEntity(url, entity, String.class);
		if (req.compareTo("delete") == 0)
			restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

	}
}
