package net.synaptology.kotlin_api_demo.user.create

import io.grpc.ManagedChannel
import io.grpc.StatusException
import io.grpc.inprocess.InProcessChannelBuilder
import java.io.File
import java.time.Duration
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.client.inject.GrpcClient
import net.synaptology.kotlin_api_demo.proto.user.UserCreateRequest
import net.synaptology.kotlin_api_demo.proto.user.UserCreateServiceGrpcKt
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
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
@SpringBootTest(
    properties = [
        "grpc.server.inProcessName=test",
        "grpc.server.port=-1",
        "grpc.client.inProcess.address=in-process:test"
    ]
)
@ActiveProfiles("test")
@DirtiesContext
@Sql(
    scripts = [
        "file:src/main/resources/db/migration/V1_0_0__create_table_users.sql",
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserCreateGrpcControllerIntegrationTests {

    companion object {

        private val channel: ManagedChannel = InProcessChannelBuilder
            .forName("test")
            .directExecutor()
            .build()

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

        @JvmStatic
        @AfterAll
        fun afterAll() {
            channel.shutdownNow()
        }
    }

    @GrpcClient("inProcess")
    val service = UserCreateServiceGrpcKt.UserCreateServiceCoroutineStub(channel)

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
}
