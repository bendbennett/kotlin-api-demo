package net.synaptology.kotlin_api_demo.user.create

import kotlin.test.Test
import net.synaptology.kotlin_api_demo.user.User
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpControllerIntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun create() {
        val inputData = InputData("John", "Doe")

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
