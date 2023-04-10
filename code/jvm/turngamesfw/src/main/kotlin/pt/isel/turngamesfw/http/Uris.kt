package pt.isel.turngamesfw.http

import org.springframework.web.util.UriTemplate

object Uris {
    const val HOME = "/"
    const val ABOUT = "/about"

    object Game {
        const val GAME_FIND = "/game/find"
        const val GAME_LEADERBOARD = "/game/leaderboard"

        const val HOME = "/game/{nameGame}"

        const val FIND = "$HOME/find"
        const val FOUND = "$HOME/found"
        const val GET_BY_ID = "$HOME/{id}"
        const val SETUP = "$HOME/setup"
        const val DO_TURN = "$HOME/turn"
        const val LEADERBOARD = "$HOME/leaderboard"
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
