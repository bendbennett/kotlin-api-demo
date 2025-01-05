package net.synaptology.kotlin_api_demo.user.create

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
class UserCreateServiceTests {

    @MockkBean
    lateinit var repository: UserRepository

    @Test
    fun create() {
        val service = UserCreateService(repository)

        val inputData = UserCreateInputData("John", "Doe")

        val userEntity = UserEntity("John", "Doe", LocalDateTime.now(), UUID.randomUUID())

        every { repository.save(any()) } returns userEntity

        val user = service.create(inputData)

        assertEquals("John", user.firstName)
        assertEquals("Doe", user.lastName)
        assertSame(LocalDateTime::class, user.createdAt::class)
        assertSame(UUID::class, user.id::class)
    }
}
