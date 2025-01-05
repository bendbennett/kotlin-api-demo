package net.synaptology.kotlin_api_demo.user.create

import java.io.File
import java.time.Duration
import kotlin.test.Test
import net.synaptology.kotlin_api_demo.user.User
import net.synaptology.kotlin_api_demo.user.advice.ErrorMessageModel
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserCreateHttpControllerIntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

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

    @Test
    @DirtiesContext
    fun create() {
        val inputData = UserCreateInputData("John", "Doe")

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(inputData, headers)

        val responseEntity: ResponseEntity<User> = restTemplate.postForEntity<User>("/user", request)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(responseEntity.body!!.firstName).isEqualTo("John")
        assertThat(responseEntity.body!!.lastName).isEqualTo("Doe")
        assertThat(responseEntity.body!!.createdAt).isNotNull()
        assertThat(responseEntity.body!!.id).isNotNull()
    }

    @Test
    @DirtiesContext
    fun `invalid first name`() {
        val inputData = JSONObject()
        inputData.put("first_name", "Jo")
        inputData.put("last_name", "Doe")

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(inputData.toString(), headers)

        val responseEntity: ResponseEntity<ErrorMessageModel> =
            restTemplate.postForEntity<ErrorMessageModel>("/user", request)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(responseEntity.body!!.timestamp).isNotNull()
        assertThat(responseEntity.body!!.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(responseEntity.body!!.error).isEqualTo(HttpStatus.BAD_REQUEST.toString())
        assertThat(responseEntity.body!!.path).isEqualTo("/user")
        assertThat(responseEntity.body!!.message).contains("first name \"Jo\" must be 3 to 100 characters in length")
    }
}
