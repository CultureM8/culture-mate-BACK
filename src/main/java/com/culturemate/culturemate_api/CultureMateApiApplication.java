package com.culturemate.culturemate_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CultureMateApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CultureMateApiApplication.class, args);
    Hello hello = new Hello();
    hello.setData("hello");
    System.out.println(hello.getData());
	}

}
