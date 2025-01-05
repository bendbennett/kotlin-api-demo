package net.synaptology.kotlin_api_demo.user.create

import java.time.LocalDateTime
import java.util.UUID
import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class UserCreateOutputDataTests {

    @Test
    fun `createdAt is string`() {
        val outputData = UserCreateOutputData("John", "Doe", LocalDateTime.now(), UUID.randomUUID())
        Assert.isInstanceOf(String::class.java, outputData.createdAt)
    }

    @Test
    fun `id is string`() {
        val outputData = UserCreateOutputData("John", "Doe", LocalDateTime.now(), UUID.randomUUID())
        Assert.isInstanceOf(String::class.java, outputData.id)
    }
}
