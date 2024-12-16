package net.synaptology.kotlin_api_demo.user

import java.time.LocalDateTime
import java.util.UUID

class User(
    var firstName: String,
    var lastName: String,
    var createdAt: LocalDateTime,
    var id: UUID
)
