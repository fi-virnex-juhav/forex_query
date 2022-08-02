package fi.virnex.juhav.forex_query;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;


@WebAppConfiguration
@ContextConfiguration(classes = ForexQueryApplication.class)
@SpringBootTest
public class ForexQueryApplicationTest {

	@Test
	void contextLoads() {
		System.out.println("ForexQueryApplicationTest.contextLoads() executed");
	}

}
