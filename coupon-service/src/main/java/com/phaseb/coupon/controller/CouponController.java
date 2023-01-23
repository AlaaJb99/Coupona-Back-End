package com.phaseb.coupon.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phaseb.coupon.model.Category;
import com.phaseb.coupon.model.Coupon;
import com.phaseb.coupon.model.PurchaseCoupon;
import com.phaseb.coupon.service.CouponService;
import com.phaseb.coupon.vo.ResponseTemplateVO;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
	
	@Autowired
	private CouponService couponService;
	
	/**
	 * 
	 * @return all coupons
	 */
	
	@PreAuthorize("hasAuthority('CUSTOMER')")
	@GetMapping
	public Iterable<Coupon> getCoupons(){
		return couponService.getAllCoupons();
	}
	
	@PreAuthorize("hasAuthority('CUSTOMER')")
	@GetMapping("/recommended")
	public Iterable<Coupon> getRecommendedCoupons(@RequestParam("customerId") long customerId){
		return couponService.knnRecommendation(customerId);
	}
	
	@PreAuthorize("hasAuthority('CUSTOMER')")
	@GetMapping("/categories")
	public Iterable<Coupon> getCouponsCategories(@RequestParam("categories") String[] categories){
		return couponService.getCouponsCategories(categories);
	}
	
	@PreAuthorize("hasAuthority('CUSTOMER')")
	@GetMapping("/customer/search")
	public Iterable<Coupon> getCouponsSearch(@RequestParam("search") String search){
		log.info("get search coupons");
		return couponService.getCouponsSearch(search);
	}

	
	/**
	 * company requests its coupons
	 * @param companyID
	 * @return coupons of this company id
	 */
	@PreAuthorize("hasAuthority('COMPANY')")
	@GetMapping(path = "/company")
	public Iterable<Coupon> getCompanyCoupons(@RequestParam("id") int companyID){
		return couponService.getCompanyCoupon(companyID);
	}
	
	/**
	 * customer searches by the coupon id
	 * @param id
	 * @return one coupon with this id
	 */
	@PreAuthorize("hasAnyAuthority('COMPANY','CUSTOMER')")
	@GetMapping(path = "/customer")
	public Coupon getCoupon(@RequestParam("couponId") int id) {
		return couponService.getOneCoupon(id);
	}
	
	/**
	 * just company has the authorization for this
	 * @param companyID
	 * @param category
	 * @return the company coupons of this category
	 */
	@GetMapping(path = "/company/{companyID}/{category}")
	public Iterable<Coupon> getCategoryCoupons(@PathVariable int companyID, @PathVariable Category category){
		return couponService.getAllCategoryCoupons(companyID, category);
	}
	
	//just the company can add a new coupon
	@PreAuthorize("hasAuthority('COMPANY')")
	@PostMapping(path = "/company")
	public Coupon addCoupon(@RequestBody Coupon coupon) {
		return couponService.addCoupon(coupon);
	}
	
	//just the company can update an existing coupon
	@PreAuthorize("hasAuthority('COMPANY')")
	@PutMapping(path = "/company")
	public boolean updateCoupon(@RequestBody Coupon coupon) {
		return couponService.updateCoupon(coupon);
	}
	
	
	//just the company can delete coupons
	@PreAuthorize("hasAuthority('COMPANY')")
	@DeleteMapping(path = "/company/coupon")
	public boolean deleteCoupon(@RequestBody Coupon coupon) {
		//when we delete it also we must delete the customer history
		return couponService.deleteCoupon(coupon);
	}
	
	//when the admin deletes a company also all it's coupons must be deleted
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteCompanyCoupons(@RequestBody Map<String, Integer> companyId){
		couponService.deleteCompanyCoupons(companyId.get("id"));
		return ResponseEntity.ok().body(null);
	}
	
	// Purchase Coupon Controllers
	
	@PreAuthorize("hasAuthority('CUSTOMER')")
	@PostMapping("/customer/purchase")
	public PurchaseCoupon purchaseCoupon(@RequestBody PurchaseCoupon coupon){
		return couponService.addCouponPurchase(coupon.getCustomerId(), coupon.getCouponId());
	}
	
	@PreAuthorize("hasAuthority('CUSTOMER')")
	@GetMapping("/customer/purchase")
	public ResponseTemplateVO getPurchasedCoupons(@RequestParam("customerId") long customerId) {
		return couponService.getPurchasedCoupons(customerId);
	}
	
	
	@DeleteMapping("/customer/delete")
	public ResponseEntity<String> deletePurchasedCoupon(@RequestBody PurchaseCoupon coupon){
		couponService.deletePurchasedCoupon(coupon);
		return ResponseEntity.ok().body(null);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/deleteAll/purchased")
	public ResponseEntity<String> deleteAllPurchasedCustomer(@RequestBody Map<String, Long> customerId){
		couponService.deleteAllPurchasedCustomer(customerId.get("customerId"));
		return ResponseEntity.ok().body(null);
	}
	
//	@GetMapping("/purchase/{customerId}")
//	public ResponseTemplateVO getPurchasedCouponsCategory(@PathVariable("customerId")long customerId) {
//		return couponService.getPurchasedCoupons(customerId);
//	}
}
