package pt.isel.turngamesfw.http

import org.springframework.web.util.UriTemplate

object Uris {
    const val HOME = "/"
    const val ABOUT = "/about"

    object Game {
        const val GAME_FIND = "/game/find"
        const val GAME_LEADERBOARD = "/game/leaderboard"

        const val GAME_INFO = "/game/{nameGame}"

        const val FIND = "$GAME_INFO/find"
        const val FOUND = "$GAME_INFO/found"
        const val GET_BY_ID = "$GAME_INFO/match/{id}"
        const val SETUP = "$GAME_INFO/setup"
        const val DO_TURN = "$GAME_INFO/turn"
        const val LEADERBOARD = "$GAME_INFO/leaderboard"
    }

    object User {
        const val REGISTER = "/user/register"
        const val LOGIN = "/user/login"
        const val LOGOUT = "/user/logout"

        const val GET_BY_ID = "/user/{id}"
        const val UPDATE = "/user/update"
        const val DELETE = "/user/delete"

        fun byId(id: String) = UriTemplate(GET_BY_ID).expand(id)
    }
}
