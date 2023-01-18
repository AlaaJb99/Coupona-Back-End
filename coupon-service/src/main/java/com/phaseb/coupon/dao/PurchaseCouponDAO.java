package com.phaseb.coupon.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phaseb.coupon.model.PurchaseCoupon;

@Repository
public interface PurchaseCouponDAO extends JpaRepository<PurchaseCoupon, Long> {

	
	public PurchaseCoupon findByCustomerIdAndCouponId(long customerId, int
	companyId);
	public List<PurchaseCoupon> findByCustomerId(long customerId);

	@Query(value = "SELECT coupon_id FROM purchased WHERE customer_id=?1", nativeQuery = true)
	public List<Integer> findCoupons(long customerId);
	
	@Transactional
	public void deleteByCouponId(int couponId);
	
	@Transactional
	public void deleteByCustomerId(long customerId);
	
	@Query(value = "SELECT coupon_id FROM purchased WHERE coupon_id=?1", nativeQuery = true)
	public List<Integer> findByCoupon(int couponId);
}
