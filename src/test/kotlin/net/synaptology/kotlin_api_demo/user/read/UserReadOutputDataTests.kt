package net.synaptology.kotlin_api_demo.user.read

import java.time.LocalDateTime
import java.util.UUID
import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class UserReadOutputDataTests {

    @Test
    fun `createdAt is string`() {
        val outputData = UserReadOutputData("John", "Doe", LocalDateTime.now(), UUID.randomUUID())
        Assert.isInstanceOf(String::class.java, outputData.createdAt)
    }

    @Test
    fun `id is string`() {
        val outputData = UserReadOutputData("John", "Doe", LocalDateTime.now(), UUID.randomUUID())
        Assert.isInstanceOf(String::class.java, outputData.id)
    }
}
