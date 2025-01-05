package net.synaptology.kotlin_api_demo.user.create

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import net.synaptology.kotlin_api_demo.proto.user.UserCreateRequest
import net.synaptology.kotlin_api_demo.user.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
class UserCreateGrpcControllerUnitTests {

    @MockkBean
    lateinit var service: IUserCreateService

    @Test
    fun create() = runBlocking {
        val grpcController = UserCreateGrpcController(service)

        val userCreateRequest = UserCreateRequest
            .newBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()

        val user = User("John", "Doe", LocalDateTime.now(), UUID.randomUUID())

        every { service.create(any()) } returns user

        val userCreateResponse = grpcController.create(userCreateRequest)

        assertEquals("John", userCreateResponse.firstName)
        assertEquals("Doe", userCreateResponse.lastName)
    }

    @Test
    fun `invalid first name`() = runBlocking {
        val grpcController = UserCreateGrpcController(service)

        val userCreateRequest = UserCreateRequest
            .newBuilder()
            .setFirstName("Jo")
            .setLastName("Doe")
            .build()

        val exception = assertThrows<IllegalArgumentException> { grpcController.create(userCreateRequest) }
        assertEquals("first name \"Jo\" must be 3 to 100 characters in length", exception.message)
    }

    @Test
    fun `service throws exception`(): Unit = runBlocking {
        every { service.create(any()) } throws Exception("test exception")

        val grpcController = UserCreateGrpcController(service)

        val userCreateRequest = UserCreateRequest
            .newBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()

        assertThrows<Exception> { grpcController.create(userCreateRequest) }
    }
}
