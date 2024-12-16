package net.synaptology.kotlin_api_demo.user.create

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class HttpController(val service: IService) {

    @RequestMapping(path = ["/"], method = [RequestMethod.GET])
    fun statusOk() {
    }

    @RequestMapping(value = ["/user"], method = [RequestMethod.POST])
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody inputData: InputData): OutputData {

        val user = service.create(inputData)

        return OutputData(
            user.firstName,
            user.lastName,
            user.createdAt,
            user.id
        )
    }
}
