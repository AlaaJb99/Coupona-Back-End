package com.phaseb.coupon.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Entity
@IdClass(PurchaseCoupon.class)
@Table(name = "purchased")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseCoupon implements Serializable {// Like A product and this it's Order From A Customer

	@Id
	private long customerId;
	@Id
	private int couponId;
}
