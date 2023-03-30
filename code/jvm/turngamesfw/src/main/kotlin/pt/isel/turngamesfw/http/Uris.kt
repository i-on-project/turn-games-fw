package pt.isel.turngamesfw.http

import org.springframework.web.util.UriTemplate

object Uris {
    const val HOME = "/"
    const val ABOUT = "/about"

    object Game {
        const val HOME = "/{game}"
        const val SEARCH = "/{game}/search"
        const val FOUND = "/{game}/found"
        const val GET_BY_ID = "/{game}/{id}"
        const val SETUP = "/{game}/setup"
        const val DO_TURN = "/{game}/turn"
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
