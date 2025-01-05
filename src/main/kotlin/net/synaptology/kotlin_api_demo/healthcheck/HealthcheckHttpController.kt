package net.synaptology.kotlin_api_demo.healthcheck

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController()
class HealthcheckHttpController {

    @RequestMapping(path = ["/"], method = [RequestMethod.GET])
    fun statusOk() {
    }
}
