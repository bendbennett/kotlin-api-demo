package net.synaptology.kotlin_api_demo.user.create

import net.synaptology.kotlin_api_demo.repositories.user.UserEntity
import net.synaptology.kotlin_api_demo.repositories.user.UserRepository
import net.synaptology.kotlin_api_demo.user.User
import org.springframework.stereotype.Service

interface IUserCreateService {
    fun create(inputData: UserCreateInputData): User
}

@Service
class UserCreateService(
    private val repository: UserRepository
) : IUserCreateService {
    override fun create(inputData: UserCreateInputData): User {
        var userEntity = UserEntity(inputData.firstName, inputData.lastName)

        userEntity = repository.save(userEntity)

        val user = User(
            userEntity.firstName,
            userEntity.lastName,
            userEntity.createdAt!!,
            userEntity.id!!
        )

        return user
    }
}
