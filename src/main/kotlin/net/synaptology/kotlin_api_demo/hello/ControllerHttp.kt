package net.synaptology.kotlin_api_demo.hello

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerHttp {

    @GetMapping("/")
    fun statusOk(){}
}
