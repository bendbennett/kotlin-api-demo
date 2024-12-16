package net.synaptology.kotlin_api_demo.user.create

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class InputDataTests {

    @Test
    fun `validate first name too short`() {
        val exception = assertThrows<IllegalArgumentException> { InputData("Jo", "Doe") }
        assertEquals("first name \"Jo\" must be 3 to 100 characters in length", exception.message)
    }

    @Test
    fun `validate first name success`() {
        assertDoesNotThrow { InputData("Jon", "Doe") }
    }

    @Test
    fun `validate last name too short`() {
        val exception = assertThrows<IllegalArgumentException> { InputData("Jon", "Do") }
        assertEquals("last name \"Do\" must be 3 to 100 characters in length", exception.message)
    }

    @Test
    fun `validate last name success`() {
        assertDoesNotThrow { InputData("Jon", "Doe") }
    }
}
