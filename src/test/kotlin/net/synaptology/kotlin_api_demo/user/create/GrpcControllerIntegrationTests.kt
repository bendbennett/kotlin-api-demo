package net.synaptology.kotlin_api_demo.user.create

import io.grpc.ManagedChannel
import io.grpc.StatusException
import io.grpc.inprocess.InProcessChannelBuilder
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.client.inject.GrpcClient
import net.synaptology.kotlin_api_demo.proto.user.UserCreateRequest
import net.synaptology.kotlin_api_demo.proto.user.UserServiceGrpcKt
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(
    properties = [
        "grpc.server.inProcessName=test",
        "grpc.server.port=-1",
        "grpc.client.inProcess.address=in-process:test"
    ]
)
@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
class GrpcControllerIntegrationTests {

    private final val channel: ManagedChannel = InProcessChannelBuilder
        .forName("test")
        .directExecutor()
        .build()

    @GrpcClient("inProcess")
    val service = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

    @Test
    @DirtiesContext
    fun create() = runBlocking {
        val userCreateRequest = UserCreateRequest
            .newBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()

        val userCreateResponse = service.create(userCreateRequest)

        assertEquals("John", userCreateResponse.firstName)
        assertEquals("Doe", userCreateResponse.lastName)
    }

    @Test
    @DirtiesContext
    fun `invalid first name`() = runBlocking {
        val userCreateRequest = UserCreateRequest
            .newBuilder()
            .setFirstName("Jo")
            .setLastName("Doe")
            .build()

        val exception = assertThrows<StatusException> { service.create(userCreateRequest) }
        assertEquals(io.grpc.Status.INVALID_ARGUMENT.code, exception.status.code)
        assertEquals("first name \"Jo\" must be 3 to 100 characters in length", exception.status.description)
    }

    @AfterAll
    fun afterAll() {
        channel.shutdownNow()
    }
}
