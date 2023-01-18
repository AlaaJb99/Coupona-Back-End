package com.phaseb.coupon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.lang.Math;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.phaseb.coupon.dao.CouponDAO;
import com.phaseb.coupon.dao.PurchaseCouponDAO;
import com.phaseb.coupon.model.Category;
import com.phaseb.coupon.model.Coupon;
import com.phaseb.coupon.model.PurchaseCoupon;
import com.phaseb.coupon.vo.ResponseTemplateVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CouponService {

	@Autowired
	private CouponDAO couponDAO;

	@Autowired
	private PurchaseCouponDAO purchaseCouponDAO;

	public Coupon getOneCoupon(int couponId) {
		return couponDAO.findById(couponId);
	}

	// then call getOneCoupon and check if it Equals
	public Coupon addCoupon(Coupon coupon) {
		// case1 no same title of this company
		Coupon addCoupon = couponDAO.findByTitleAndCompanyID(coupon.getTitle(), coupon.getCompanyID());
		if (addCoupon != null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title Exists");
		return couponDAO.save(coupon);
	}

	public boolean updateCoupon(Coupon coupon) {
		Coupon updateCoupon = couponDAO.findByIdAndCompanyID(coupon.getId(), coupon.getCompanyID());
		if (updateCoupon == null)
			return false;// there is not such a coupon with this id for this company
		if (updateCoupon.getTitle().compareTo(coupon.getTitle()) != 0) { // the title will update
			Coupon c = couponDAO.findByTitleAndCompanyID(coupon.getTitle(), coupon.getCompanyID());
			if (c != null)// there is a coupon with this title for another coupon for this company
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title Exists");
		}
		updateCoupon.setCategory(coupon.getCategory());
		updateCoupon.setTitle(coupon.getTitle());
		updateCoupon.setDescription(coupon.getDescription());
		updateCoupon.setStartDate(coupon.getStartDate());
		updateCoupon.setEndDate(coupon.getEndDate());
		updateCoupon.setAmount(coupon.getAmount());
		updateCoupon.setPrice(coupon.getPrice());
		couponDAO.save(updateCoupon);
		return true;
	}

	public boolean deleteCoupon(Coupon coupon) {
		// company wants to delete this coupon
		// 1 delete from coupon table
		couponDAO.delete(coupon);
		// delete it from the purchasseCoupon table
		purchaseCouponDAO.deleteByCouponId(coupon.getId());
		return true;
	}

	public Iterable<Coupon> getAllCategoryCoupons(int companyId, Category category) {
		return couponDAO.findByCompanyIDAndCategory(companyId, category);
	}

	public List<Coupon> getAllCoupons() {
		return couponDAO.findAll();
	}

	public List<Coupon> getCouponsCategories(String[] categories) {
		List<Coupon> couponsCategories = new ArrayList<>();
		for (String category : categories) {
			couponsCategories.addAll(couponDAO.findByCategory(Category.valueOf(category)));
		}
		return couponsCategories;
	}

	public Iterable<Coupon> getCompanyCoupon(int companyId) {
		return couponDAO.findByCompanyID(companyId);
	}

	public Set<Coupon> getCouponsSearch(String search) {
		Set<Coupon> searchCoupons = new TreeSet<>();
		searchCoupons.addAll(couponDAO.findByTitleContaining(search));
		searchCoupons.addAll(couponDAO.findByDescriptionContaining(search));
		return searchCoupons;
	}

	/* Purchasing function */

	public PurchaseCoupon addCouponPurchase(long customerId, int couponId) {
		PurchaseCoupon puCoupon = purchaseCouponDAO.findByCustomerIdAndCouponId(customerId, couponId);
		// check if this customer already purchased this coupon
		if (puCoupon != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already purchased");
		}

		Coupon coupon = couponDAO.findById(couponId);
		// Check if it's in stock (quantity is more than one)
		if (coupon.getAmount() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saled Out");
		;
		coupon.setAmount(coupon.getAmount() - 1);
		couponDAO.save(coupon);
		// create the new purchase entity
		PurchaseCoupon purchaseCoupon = new PurchaseCoupon(customerId, couponId);
		// add the purchase and return it
		return purchaseCouponDAO.save(purchaseCoupon);
	}

	public void deletePurchasedCoupon(PurchaseCoupon coupon) {
		// a customer made this request so delete a specific coupon for this customer id
		purchaseCouponDAO.delete(coupon);
	}

	public void deleteAllPurchasedCustomer(long id) {
		purchaseCouponDAO.deleteByCustomerId(id);
	}

	public ResponseTemplateVO getPurchasedCoupons(long customerId) {
		List<Integer> pc = purchaseCouponDAO.findCoupons(customerId);
		ResponseTemplateVO vo = new ResponseTemplateVO();
		vo.setCustomerId(customerId);
		List<Coupon> coupons = couponDAO.findAllById(pc);
		vo.setCoupon(coupons);
		log.info(vo.getCoupon().toString());
		return vo;
	}

	public void deleteCompanyCoupons(int id) {
		Iterable<Coupon> companyCoupons = couponDAO.findByCompanyID(id);
		// delete from the coupons table
		couponDAO.deleteAll(companyCoupons);
		// delete also the purchased coupons
		companyCoupons.forEach(coupon -> {
			purchaseCouponDAO.deleteByCouponId(coupon.getId());
		});
	}

	public void deleteExpiredCoupons() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate today = LocalDate.now();// LocalDate.parse(.format(formatter),formatter);
		String todayDate = today.format(formatter);
		List<Coupon> deleteCoupons = couponDAO.findByEndDate(todayDate);
		couponDAO.deleteAll(deleteCoupons);
		for (Coupon coupon : deleteCoupons) {
			purchaseCouponDAO.deleteByCouponId(coupon.getId());
		}

	}

	// Recommendation Algorithm Functions

	/**
	 * 
	 * @param customers who purchased coupons
	 * @param coupons   all the coupons in the system
	 * @param cuP       the purchased coupons with the customers id
	 * @return a matrix with the customers as the rows and coupons as the column
	 *         values in the matrix 0/1 1 in the ij place if customer i purchased
	 *         the coupon 0 otherwise
	 */
	private int[][] interactionMatrix(List<Long> customers, List<Integer> coupons, List<PurchaseCoupon> cuP) {
		// build the customer-coupon matrix
		int[][] matrix = new int[customers.size()][coupons.size()];

		for (PurchaseCoupon cu : cuP) {
			int custoIndex = customers.indexOf(cu.getCustomerId());
			int couIndex = coupons.indexOf(cu.getCouponId());
			matrix[custoIndex][couIndex] = 1;
		}

		return matrix;
	}

	/**
	 * 
	 * @param v1 a vector in a matrix
	 * @param v2 a vector in a matrix
	 * @return the multiplication of those two vectors
	 */
	private double multiplyVectors(int[] v1, int[] v2) {
		int result = 0;
		for (int i = 0; i < v1.length; i++)
			result += v1[i] * v2[i];
		return result;
	}

	/**
	 * 
	 * @param v
	 * @return the length of the vector = |v| = sqr(v^2)
	 */
	private double lengthVector(int[] v) {
		double result = 0;
		for (int i = 0; i < v.length; i++) {
			result = result + (v[i] * v[i]);
		}
		result = Math.sqrt(result);
		return result;
	}

	/**
	 * 
	 * @param interMatrix the interaction matrix of the customers
	 * @param n           the row number = the customer who bought coupons
	 * @return userSim the user similarity matrix by using cosine similarity
	 */
	private double[][] userSimilarity(int[][] interMatrix, int n) {
		double[][] userSim = new double[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				double multiply = multiplyVectors(interMatrix[i], interMatrix[j]);
				double lenCi = lengthVector(interMatrix[i]);
				double lenCj = lengthVector(interMatrix[j]);
				userSim[i][j] = multiply / (lenCi * lenCj);
			}
		return userSim;
	}

	private int[] findKNearestCustomers(double[] similar, int k) {
		double[] similarCusto = Arrays.copyOf(similar, similar.length);
		int[] indexes = new int[similar.length];
		for (int i = 0; i < k; i++) {
			double maxi = Double.MIN_VALUE;
			for (int j = 0; j < similar.length; j++) {
				if (maxi < similarCusto[j]) {
					maxi = similarCusto[j];
					indexes[i] = j;
				}
				similarCusto[indexes[i]] = Double.MIN_VALUE;
			}
		}
		return indexes;

	}

	private List<Coupon> getRecommendedCoupons(int[] customersIndex, int[][] interactionMatrix, List<Integer> couponsId,
			int thisCustomerIndex) {
		List<Coupon> recommendedCoupons = new ArrayList<>();
		int[] thisCust = interactionMatrix[thisCustomerIndex];
		// get over all the customers coupons
		for (int i = 0; i < customersIndex.length; i++) {
			int[] customer = interactionMatrix[customersIndex[i]];
			// get the coupons that this customer buy and the other didn't
			for (int j = 0; j < customer.length; j++) {
				if (customer[j] == 1 && thisCust[j] == 0) {
					int couponIndex = couponsId.get(j);
					Coupon coupon = couponDAO.findById(couponIndex);
					recommendedCoupons.add(coupon);
				}
			}
		}
		return recommendedCoupons;
	}

	public List<Coupon> knnRecommendation(long c, int k) {
		List<PurchaseCoupon> purchased = purchaseCouponDAO.findAll();
		if (purchased.size() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no one purchased coupons yet");
		else if (purchaseCouponDAO.findByCustomerId(c).size() == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no one purchased coupons yet");
		} else {
			// get the data from the database
			// get all the coupons
			List<Integer> couponsId = couponDAO.findAllCouponId();
			ArrayList<Long> customerId = new ArrayList<>();
			// get all the purchasing history
			List<PurchaseCoupon> cuP = purchaseCouponDAO.findAll();
			// remove all the duplicates and create one list of the customers who at least
			// purchased one coupon
			for (PurchaseCoupon cu : cuP)
				if (!(customerId.contains(cu.getCustomerId())))
					customerId.add(cu.getCustomerId());

			// “user-item interactions matrix”.
			int[][] matrix = interactionMatrix(customerId, couponsId, cuP);

			// calculate the user-user similarity matrix
			double[][] userSimilarity = userSimilarity(matrix, matrix.length);

			// find the k nearest customers to the c customer
			log.info(customerId.indexOf(c) + "");
			double[] similarCustomers = userSimilarity[customerId.indexOf(c)];
			int[] kindex = findKNearestCustomers(similarCustomers, k);

			// find the coupons that these customers bought and this customer didn't
			List<Coupon> recommCoupons = getRecommendedCoupons(kindex, matrix, couponsId, customerId.indexOf(c));

			return recommCoupons;
		}
	}
}
