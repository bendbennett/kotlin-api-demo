package net.synaptology.kotlin_api_demo.user.create

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserCreateHttpController(val service: IUserCreateService) {

    @RequestMapping(value = ["/user"], method = [RequestMethod.POST])
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody inputData: UserCreateInputData): UserCreateOutputData {

        val user = service.create(inputData)

        return UserCreateOutputData(
            user.firstName,
            user.lastName,
            user.createdAt,
            user.id
        )
    }
}
