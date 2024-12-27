package net.synaptology.kotlin_api_demo.repositories.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name="users")
class UserEntity(
    var firstName: String,
    var lastName: String,
    var createdAt: LocalDateTime? = LocalDateTime.now(),
    @Id @GeneratedValue var id: UUID? = null
)
