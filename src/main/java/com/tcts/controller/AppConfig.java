package com.tcts.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
	@Bean
	   public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		System.out.println("AppConfig.propertySourcesPlaceholderConfigurer()");
	      return new PropertySourcesPlaceholderConfigurer();

	   }


}
