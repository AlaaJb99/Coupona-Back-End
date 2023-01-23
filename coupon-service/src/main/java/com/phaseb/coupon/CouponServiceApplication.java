package com.phaseb.coupon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.phaseb.coupon.model.Category;
import com.phaseb.coupon.model.Coupon;
import com.phaseb.coupon.service.CouponService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class CouponServiceApplication {

	public static void main(String[] args) {
		
		
		SpringApplication.run(CouponServiceApplication.class, args);
		
	}
	
	@Bean
	CommandLineRunner runner(CouponService couponService) {
		return args -> {
			TimerTask repeatedTask = new TimerTask() {
			public void run() {
				log.info("delete Expired Coupons");
				couponService.deleteExpiredCoupons();
			}
		};
		Timer timer = new Timer("Timer");
		timer.schedule(repeatedTask, 1000*60*60*24);
		couponService.deleteExpiredCoupons();
//			try {
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//				String delimiter=",";
//				File file = new File(
//						"C:\\Users\\Alaa Jbareen\\Desktop\\Semester 9\\Final Project\\Data\\Coupons.csv");
//				FileReader fr = new FileReader(file);
//				BufferedReader br = new BufferedReader(fr);
//				String line = " ";
//				String[] tempArr;
//				while ((line = br.readLine()) != null) {
//					//log.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//					tempArr = line.split(delimiter);
//					couponService.addCoupon(new Coupon(0, Integer.parseInt(tempArr[1]),
//							Category.valueOf(tempArr[2]), tempArr[3], tempArr[4],
//							LocalDate.parse(tempArr[5], formatter), LocalDate.parse(tempArr[6], formatter),
//							Integer.parseInt(tempArr[7]), Integer.parseInt(tempArr[8])));
//
//					System.out.println();
//				}
//				br.close();
//			} catch (IOException ioe) {
//				ioe.printStackTrace();
//			}
		};
	}

}
