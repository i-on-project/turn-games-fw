package pt.isel.turngamesfw.domain

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
    val status: Status,
) {
    enum class Status {
        OFFLINE,
        ONLINE,
    }

    data class PasswordValidationInfo(
        val validationInfo: String
    )

    data class Stats (
        val status: Status,
        val rating: Int,
    ) {
        enum class Status {
            INACTIVE,
            SEARCHING,
            IN_GAME,
        }
    }
}
