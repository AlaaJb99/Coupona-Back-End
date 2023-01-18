package com.phaseb.coupon.vo;

import java.util.List;

import com.phaseb.coupon.model.Coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTemplateVO {

	private long customerId;
	private List<Coupon> coupon;

}
