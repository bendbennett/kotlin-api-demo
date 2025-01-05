package net.synaptology.kotlin_api_demo.user.read

import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import java.io.File
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.client.inject.GrpcClient
import net.synaptology.kotlin_api_demo.proto.user.UserReadRequest
import net.synaptology.kotlin_api_demo.proto.user.UserReadServiceGrpcKt
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
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
        "file:src/test/resources/db/seed/insert_table_users.sql",
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserReadGrpcControllerIntegrationTests {

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
    val service = UserReadServiceGrpcKt.UserReadServiceCoroutineStub(channel)

    @Test
    @DirtiesContext
    fun read(): Unit = runBlocking {
        val userReadRequest = UserReadRequest
            .newBuilder()
            .build()

        val usersReadResponse = service.read(userReadRequest)

        assertEquals(2, usersReadResponse.usersCount)

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
}
