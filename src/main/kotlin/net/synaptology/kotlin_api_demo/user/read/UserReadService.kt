package net.synaptology.kotlin_api_demo.user.read

import net.synaptology.kotlin_api_demo.repositories.user.UserRepository
import net.synaptology.kotlin_api_demo.user.User
import org.springframework.stereotype.Service

interface IUserReadService {
    fun read(): List<User>
}

@Service
class UserReadService(
    private val repository: UserRepository
) : IUserReadService {
    override fun read(): List<User> {

        val userEntities = repository.findAll()
        val userEntitiesIterator = userEntities.iterator()
        val users = mutableListOf<User>()

//        val modelMapper = ModelMapper
//
//        userEntities.toList().stream().map(modelMapper.map())

        while (userEntitiesIterator.hasNext()) {
            val userEntity = userEntitiesIterator.next()

            val user = User(
                userEntity.firstName,
                userEntity.lastName,
                userEntity.createdAt!!,
                userEntity.id!!
            )

            users.add(user)
        }

        return users
    }
}
