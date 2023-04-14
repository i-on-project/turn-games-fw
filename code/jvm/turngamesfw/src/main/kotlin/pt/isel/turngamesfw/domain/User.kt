package pt.isel.turngamesfw.domain

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
    val status: Status = Status.OFFLINE,
) {
    enum class Status {
        OFFLINE,
        ONLINE,
    }

    data class PasswordValidationInfo(
        val validationInfo: String
    )

    data class Stats (
        val state: State,
        val rating: Int,
    ) {
        enum class State {
            INACTIVE,
            SEARCHING,
            IN_GAME,
        }
    }
}

data class LeaderboardUser(
    val username: String,
    val rating: Int,
    val position: Int
)
