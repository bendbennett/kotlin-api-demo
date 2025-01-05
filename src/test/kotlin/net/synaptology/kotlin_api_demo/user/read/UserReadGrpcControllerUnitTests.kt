package net.synaptology.kotlin_api_demo.user.read

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.runBlocking
import net.synaptology.kotlin_api_demo.proto.user.UserReadRequest
import net.synaptology.kotlin_api_demo.user.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
class UserReadGrpcControllerUnitTests {

    @MockkBean
    lateinit var service: IUserReadService

    @Test
    fun read(): Unit = runBlocking {
        val grpcController = UserReadGrpcController(service)

        val userReadRequest = UserReadRequest
            .newBuilder()
            .build()

        val users = listOf(
            User("John", "Doe", LocalDateTime.now(), UUID.randomUUID()),
            User("Jane", "Doe", LocalDateTime.now(), UUID.randomUUID())
        )

        every { service.read() } returns users

        val usersReadResponse = grpcController.read(userReadRequest)

        val userOne = usersReadResponse.getUsers(0)

        assertIs<String>(userOne.id)
        assertEquals("John", userOne.firstName)
        assertEquals("Doe", userOne.lastName)
        assertIs<String>(userOne.createdAt)

        val userTwo = usersReadResponse.getUsers(1)

        assertIs<String>(userTwo.id)
        assertEquals("Jane", userTwo.firstName)
        assertEquals("Doe", userTwo.lastName)
        assertIs<String>(userTwo.createdAt)
    }

    @Test
    fun `service throws exception`(): Unit = runBlocking {
        every { service.read() } throws Exception("test exception")

        val grpcController = UserReadGrpcController(service)

        val userReadRequest = UserReadRequest
            .newBuilder()
            .build()

        assertThrows<Exception> { grpcController.read(userReadRequest) }
    }
}
