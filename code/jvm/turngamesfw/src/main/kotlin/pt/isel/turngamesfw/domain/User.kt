package pt.isel.turngamesfw.domain

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
    val status: Status,
    val rating: Int,
) {
    enum class Status {
        OFFLINE,
        ONLINE,
        SEARCHING,
        IN_GAME,
    }

    data class PasswordValidationInfo(
        val validationInfo: String
    )
}
