package net.synaptology.kotlin_api_demo.repositories.user

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long>
