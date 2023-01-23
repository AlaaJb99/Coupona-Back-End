package com.phaseb.coupon.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.phaseb.coupon.model.Category;
import com.phaseb.coupon.model.Coupon;

@Repository
public interface CouponDAO extends JpaRepository<Coupon, Integer> {
	
	public Coupon findById(int id);
	
	public Coupon findByIdAndCompanyID(int id, int companyID);
	
	public Coupon findByTitleAndCompanyID(String title, int companyID);

	public Iterable<Coupon> findByCompanyIDAndCategory(int companyID, Category category);
	
	public Iterable<Coupon> findByCompanyID(int companyID);
	
	@Query(value = "SELECT id FROM coupons", nativeQuery = true)
	public List<Integer> findAllCouponId();
	
	public List<Coupon> findByEndDate(LocalDate endDate);
	
	public List<Coupon> findByCategory(Category category);
	
	public List<Coupon> findByTitleContaining(String title);
	
	public List<Coupon> findByDescriptionContaining(String description);

}
