package com.virnex.juhav.CurrencyExchangeQuery;

/**
 * @author Juha Valimaki, new guy at virnex.fi 2022-06-19, June 19 2022
 *
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.virnex.juhav.CurrencyExchangeQuery"})
public class CurrencyExchangeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeApplication.class, args);
	}

}
