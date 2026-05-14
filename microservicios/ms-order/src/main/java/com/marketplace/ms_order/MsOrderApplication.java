package com.marketplace.ms_order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.marketplace")
public class MsOrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsOrderApplication.class, args);
	}
}