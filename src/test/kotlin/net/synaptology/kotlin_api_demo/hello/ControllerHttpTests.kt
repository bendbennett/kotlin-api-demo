package net.synaptology.kotlin_api_demo.hello

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest
@SpringJUnitConfig(classes = [ControllerHttpConfiguration::class])
class ControllerHttpTests(@Autowired val mockMvc: MockMvc) {

    @Test
    fun status_ok() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
    }
}