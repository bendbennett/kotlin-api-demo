package net.synaptology.kotlin_api_demo.user.create

data class UserCreateInputData(
    val firstName: String,
    val lastName: String
) {
    init {
        require(firstName.length in 3..100) {
            "first name \"$firstName\" must be 3 to 100 characters in length"
        }
        require(lastName.length in 3..100) {
            "last name \"$lastName\" must be 3 to 100 characters in length"
        }
    }
}
