package com.luckymarket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = {RedisRepositoriesAutoConfiguration.class})
class LuckymarketApplicationTests {

	@Test
	void contextLoads() {
	}

}
