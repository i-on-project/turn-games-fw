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

data class UserDetailsOutputModel(
    val id: Int,
    val username: String,
    val status: User.Status
)

fun User.toUserDetailsOutputModel() =
    UserDetailsOutputModel(
        this.id,
        this.username,
        this.status,
    )
