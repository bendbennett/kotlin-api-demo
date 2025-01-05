package net.synaptology.kotlin_api_demo.user.read

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.time.LocalDateTime
import java.util.UUID
import net.synaptology.kotlin_api_demo.user.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [UserReadHttpController::class])
class UserReadHttpControllerUnitTests(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var service: IUserReadService

    @Test
    fun create() {
        val userOne = User("John", "Doe", LocalDateTime.now(), UUID.randomUUID())
        val userTwo = User("Jane", "Smith", LocalDateTime.now(), UUID.randomUUID())

        every { service.read() } returns listOf(userOne, userTwo)

        mockMvc.perform(
            get("/user")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$[0].first_name").value("John"))
            .andExpect(jsonPath("\$[0].last_name").value("Doe"))
            .andExpect(jsonPath("\$[1].first_name").value("Jane"))
            .andExpect(jsonPath("\$[1].last_name").value("Smith"))
    }

    @Test
    fun `service throws exception`() {
        every { service.read() } throws Exception("test exception")

        mockMvc.perform(
            get("/user")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
    }
}
