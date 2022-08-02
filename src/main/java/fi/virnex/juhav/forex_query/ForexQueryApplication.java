package fi.virnex.juhav.forex_query;

/**
 * @author Juha Valimaki, new guy at virnex.fi 2022-07-05, July 05 2022
 *
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"fi.virnex.juhav.forex_query"})
public class ForexQueryApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ForexQueryApplication.class, args);
	}

}
