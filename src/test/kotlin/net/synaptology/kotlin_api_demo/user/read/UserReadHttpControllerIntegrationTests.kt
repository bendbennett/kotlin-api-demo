package net.synaptology.kotlin_api_demo.user.read

import java.io.File
import java.time.Duration
import net.synaptology.kotlin_api_demo.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@Sql(
    scripts = [
        "file:src/main/resources/db/migration/V1_0_0__create_table_users.sql",
        "file:src/test/resources/db/seed/insert_table_users.sql",
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserReadHttpControllerIntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    companion object {

        @Container
        @JvmStatic
        val composeContainer: ComposeContainer = ComposeContainer(File("docker/test/docker-compose.yml"))
            .withExposedService(
                "kotlin-api-demo-db-test-1",
                5432,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
            )

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            val serviceHost = composeContainer.getServiceHost("kotlin-api-demo-db-test-1", 5432)
            val servicePort = composeContainer.getServicePort("kotlin-api-demo-db-test-1", 5432)

            registry.add("spring.datasource.url") { "jdbc:postgresql://${serviceHost}:${servicePort}/kotlin-api-demo?currentSchema=public" }
            registry.add("spring.datasource.username") { "\${postgres.username}" }
            registry.add("spring.datasource.password") { "\${postgres.password}" }
        }
    }

    // https://stackoverflow.com/questions/39679180/kotlin-call-java-method-with-classt-argument
    // https://stackoverflow.com/questions/45949584/how-does-the-reified-keyword-in-kotlin-work
    private inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> =
        object : ParameterizedTypeReference<T>() {}

    @Test
    @DirtiesContext
    fun read() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(null, headers)

        val response = restTemplate.exchange("/user", HttpMethod.GET, request, typeRef<List<User>>())

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        assertThat(response.body!!.count()).isEqualTo(2)

        assertThat(response.body!![0].firstName).isEqualTo("John")
        assertThat(response.body!![0].lastName).isEqualTo("Doe")
        assertThat(response.body!![0].createdAt).isNotNull()
        assertThat(response.body!![0].id).isNotNull()

        assertThat(response.body!![1].firstName).isEqualTo("Jane")
        assertThat(response.body!![1].lastName).isEqualTo("Doe")
        assertThat(response.body!![1].createdAt).isNotNull()
        assertThat(response.body!![1].id).isNotNull()
    }
}
