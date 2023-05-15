package pt.isel.turngamesfw.http

import org.springframework.web.util.UriTemplate

object Uris {
    const val HOME = "/"
    const val ABOUT = "/about"

    object Game {
        const val INFO = "/game/{nameGame}"
        const val FIND = "$INFO/find"
        const val FOUND = "$INFO/found"
        const val MATCH = "$INFO/match/{id}"
        const val SETUP = "$INFO/setup"
        const val DO_TURN = "$INFO/turn"
        const val LEADERBOARD = "$INFO/leaderboard"

        fun infoByGameName(gameName: String) = UriTemplate(INFO).expand(gameName)
        fun findByGameName(gameName: String) = UriTemplate(FIND).expand(gameName)
        fun foundByGameName(gameName: String) = UriTemplate(FOUND).expand(gameName)
        fun leaderboardByGameName(gameName: String) = UriTemplate(LEADERBOARD).expand(gameName)
        fun matchById(gameName: String, matchId: String) = UriTemplate(MATCH).expand(gameName, matchId)
        fun doTurnByGameName(gameName: String) = UriTemplate(DO_TURN).expand(gameName)
        fun setupByGameName(gameName: String) = UriTemplate(SETUP).expand(gameName)
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
