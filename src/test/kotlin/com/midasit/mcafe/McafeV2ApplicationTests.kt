package com.midasit.mcafe

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["h2"])
class McafeV2ApplicationTests {

	@Test
	fun contextLoads() {
	}
}
