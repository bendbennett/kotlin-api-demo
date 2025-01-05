package net.synaptology.kotlin_api_demo.user.read

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserReadHttpController(val service: IUserReadService) {

    @RequestMapping(value = ["/user"], method = [RequestMethod.GET])
    @ResponseStatus(HttpStatus.OK)
    fun read(): List<UserReadOutputData> {

        val users = service.read()

        val usersIterator = users.iterator()
        val outputData = mutableListOf<UserReadOutputData>()

        while (usersIterator.hasNext()) {
            val user = usersIterator.next()

            val outputDatum = UserReadOutputData(
                user.firstName,
                user.lastName,
                user.createdAt,
                user.id
            )

            outputData.add(outputDatum)
        }

        return outputData
    }
}
