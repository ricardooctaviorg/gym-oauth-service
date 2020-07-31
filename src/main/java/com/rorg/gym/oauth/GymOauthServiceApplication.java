package com.rorg.gym.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients

public class GymOauthServiceApplication implements CommandLineRunner {
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(GymOauthServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String password = "S1VAL3W3LLC0MXP}}";
		for (int i = 0; i < 4; i++) 
			System.out.println(i +" : "+ bCryptPasswordEncoder.encode(password));
	}

}
