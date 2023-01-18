package com.phaseb.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phaseb.customer.model.Customer;
import com.phaseb.customer.service.CustomerService;
import com.phaseb.customer.vo.ResponseTemplateVO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin")
	public ResponseEntity<Iterable<Customer>> getAllCustomers() {
		return ResponseEntity.ok().body(customerService.getAllCustomers());
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin")
	public Customer addCustomer(@RequestBody Customer customer) {
		log.info("add controller " +customer.getEmail());
		return customerService.addCustomer(customer);
	}

	// this method can be accessed by admin and customer, admin can see the customer
	// details,
	// the customer can view his details but can't edit them
	@PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
	@GetMapping("/customer")
	public Customer getCustomer(@RequestParam("email") String email) {
		return customerService.getCustomer(email);
	}

	@PreAuthorize("hasAuthority('CUSTOMER')")
	@GetMapping("/customer/coupons")
	public ResponseTemplateVO getCustomerWithCoupons(@RequestBody CustomerId id) {
		return customerService.getCustomerWihCoupons(id.getId());
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/admin")
	public Customer updateCustomer(@RequestBody Customer customer) {
		return customerService.updateCustomer(customer);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/admin")
	public void deleteCustomer(@RequestBody Customer customer) {
		log.info("Controller - delete customer");
		customerService.deleteCustomer(customer);
	}
}

@Data
class CustomerId {
	private long id;
}
