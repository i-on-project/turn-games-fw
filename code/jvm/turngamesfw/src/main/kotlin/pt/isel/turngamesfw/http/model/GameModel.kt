package pt.isel.turngamesfw.http.model

import java.util.UUID

data class SetupInputModel(
    val matchId: UUID,
    val info: Any,
)

data class TurnInputModel(
    val matchId: UUID,
    val info: Any,
)

data class LeaderBoardInputModel(
    val gameName: String,
    val limit: Int,
    val page: Int,
)

data class GameNameInputModel(
    val gameName: String,
)