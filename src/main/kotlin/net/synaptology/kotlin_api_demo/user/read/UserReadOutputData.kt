package net.synaptology.kotlin_api_demo.user.read

import java.time.LocalDateTime
import java.util.UUID

data class UserReadOutputData(
    val firstName: String,
    val lastName: String,
    private val createdAtAsLocalDateTime: LocalDateTime,
    private val idAsUuid: UUID,
) {
    val createdAt = createdAtAsLocalDateTime.toString()
    var id = idAsUuid.toString()
}
