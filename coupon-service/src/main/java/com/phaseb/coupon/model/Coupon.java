package com.phaseb.coupon.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon implements Comparable<Coupon> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int companyID;
	@Enumerated(EnumType.STRING)
	private Category category;
	private String title;
	@Column( length = 1000000 )
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private int amount;
	private int price;

	/// private Image image;
	@Override
	public int compareTo(Coupon o) {
		if (this.id == o.id)
			return 0;
		if (this.id < o.id)
			return -1;
		return 1;
	}
}
