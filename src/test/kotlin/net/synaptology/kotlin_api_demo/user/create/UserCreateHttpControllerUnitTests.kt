package net.synaptology.kotlin_api_demo.user.create

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [UserCreateHttpController::class])
class UserCreateHttpControllerUnitTests(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var service: IUserCreateService

    @Test
    fun create() {
        val user = User("John", "Doe", LocalDateTime.now(), UUID.randomUUID())

        every { service.create(any()) } returns user

        mockMvc.perform(
            post("/user")
                .content("{\"first_name\":\"John\", \"last_name\": \"Doe\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.first_name").value("John"))
            .andExpect(jsonPath("\$.last_name").value("Doe"))
    }

    @Test
    fun `invalid json`() {
        mockMvc.perform(
            post("/user")
                .content("{\"first_name\":\"Jo\", \"last_name\": \"Doe\"")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.status").value(HttpStatus.BAD_REQUEST.value()))
    }

    @Test
    fun `invalid first name`() {
        mockMvc.perform(
            post("/user")
                .content("{\"first_name\":\"Jo\", \"last_name\": \"Doe\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.status").value(HttpStatus.BAD_REQUEST.value()))
    }

    @Test
    fun `service throws exception`() {
        every { service.create(any()) } throws Exception("test exception")

        mockMvc.perform(
            post("/user")
                .content("{\"first_name\":\"John\", \"last_name\": \"Doe\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
    }
}