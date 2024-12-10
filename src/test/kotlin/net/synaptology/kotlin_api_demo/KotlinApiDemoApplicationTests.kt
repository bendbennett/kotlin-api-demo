package net.synaptology.kotlin_api_demo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.assertNotNull

@SpringBootTest
class KotlinApiDemoApplicationTests {

	@Test
	fun contextLoads(context: ApplicationContext) {
		assertNotNull(context)
	}

}
