package net.synaptology.kotlin_api_demo

import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinApiDemoApplicationTests {

    @Test
    fun contextLoads(context: ApplicationContext) {
        assertNotNull(context)
    }

}
