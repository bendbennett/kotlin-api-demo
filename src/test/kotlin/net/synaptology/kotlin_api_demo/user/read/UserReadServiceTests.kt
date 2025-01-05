package net.synaptology.kotlin_api_demo.user.read

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertSame
import net.synaptology.kotlin_api_demo.repositories.user.UserEntity
import net.synaptology.kotlin_api_demo.repositories.user.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
class UserReadServiceTests {

    @MockkBean
    lateinit var repository: UserRepository

    @Test
    fun create() {
        val service = UserReadService(repository)

        val userEntityOne = UserEntity("John", "Doe", LocalDateTime.now(), UUID.randomUUID())
        val userEntityTwo = UserEntity("Jane", "Smith", LocalDateTime.now(), UUID.randomUUID())

        every { repository.findAll() } returns listOf(userEntityOne, userEntityTwo)

        val users = service.read()

        assertEquals(2, users.size)

        assertEquals("John", users[0].firstName)
        assertEquals("Doe", users[0].lastName)
        assertSame(LocalDateTime::class, users[0].createdAt::class)
        assertSame(UUID::class, users[0].id::class)

        assertEquals("Jane", users[1].firstName)
        assertEquals("Smith", users[1].lastName)
        assertSame(LocalDateTime::class, users[1].createdAt::class)
        assertSame(UUID::class, users[1].id::class)
    }
}
