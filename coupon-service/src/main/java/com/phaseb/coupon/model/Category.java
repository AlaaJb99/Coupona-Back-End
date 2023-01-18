package com.phaseb.coupon.model;

import javax.persistence.Table;

@Table(name = "category")
public enum Category {
	Food, Furniture, Clothing, Electricity, Electronics, Cosmetics, Resturant, Education;
}

