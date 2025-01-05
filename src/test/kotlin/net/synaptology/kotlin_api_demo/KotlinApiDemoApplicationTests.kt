package net.synaptology.kotlin_api_demo

import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class KotlinApiDemoApplicationTests {

    @Test
    fun contextLoads(context: ApplicationContext) {
        assertNotNull(context)
    }

}
