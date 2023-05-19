package pt.isel.turngamesfw.http.model

import com.fasterxml.jackson.annotation.JsonUnwrapped
import pt.isel.turngamesfw.domain.User

data class RegisterInputModel(
    val username: String,
    val password: String,
)

data class LoginInputModel(
    val username: String,
    val password: String,
)

data class UpdateUserInputModel(
    val username: String,
)

data class UserDetailsOutputModel(
    val id: Int,
    val username: String,
    val status: User.Status
) {
    companion object {
        val clazz = "user"
    }
}

data class UserCreationOutputModel(
    val details: UserDetailsOutputModel,
) {
    companion object {
        val clazz = "userCreation"
    }
}

fun User.toUserDetailsOutputModel() =
    UserDetailsOutputModel(
        this.id,
        this.username,
        this.status,
    )
