package com.culturemate.culturemate_api;

import com.culturemate.culturemate_api.init.RegionInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CultureMateApiApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CultureMateApiApplication.class, args);
    Hello hello = new Hello();
    hello.setData("hello");
    System.out.println(hello.getData());
//    RegionInitializer.regionInit();
	}

}
