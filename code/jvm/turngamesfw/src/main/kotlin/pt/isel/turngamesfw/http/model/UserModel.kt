package pt.isel.turngamesfw.http.model

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