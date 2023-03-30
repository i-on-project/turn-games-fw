package pt.isel.turngamesfw.http

import org.springframework.web.util.UriTemplate

object Uris {
    const val HOME = "/"
    const val ABOUT = "/about"

    object Game {
        const val GAME_HOME = "/game/{nameGame}"

        const val FIND = "$GAME_HOME/find"
        const val FOUND = "$GAME_HOME/found"
        const val GET_BY_ID = "$GAME_HOME/{id}"
        const val SETUP = "$GAME_HOME/setup"
        const val DO_TURN = "$GAME_HOME/turn"
    }

    object User {
        const val REGISTER = "/user/register"
        const val LOGIN = "/user/login"
        const val LOGOUT = "/user/logout"

        const val GET_BY_ID = "/user/{id}"
        const val UPDATE = "/user/update"
        const val DELETE = "/user/delete"
        const val LEADERBOARD = "/user/leaderboard"

        fun byId(id: String) = UriTemplate(GET_BY_ID).expand(id)
    }
}
