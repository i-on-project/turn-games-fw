package pt.isel.turngamesfw.http

import org.springframework.web.util.UriTemplate

object Uris {
    const val HOME = "/"
    const val ABOUT = "/about"

    object Game {
        const val ALL_GAMES = "/gameList"
        const val INFO = "/game/{nameGame}"
        const val FIND = "$INFO/find"
        const val FOUND = "$INFO/found"
        const val MY_STATE = "$INFO/mystate"
        const val MATCH = "$INFO/match/{id}"
        const val SETUP = "$INFO/setup"
        const val DO_TURN = "$INFO/turn"
        const val MY_TURN = "$INFO/{id}/myturn"
        const val LEADERBOARD = "$INFO/leaderboard"

        fun infoByGameName(gameName: String) = UriTemplate(INFO).expand(gameName)
        fun findByGameName(gameName: String) = UriTemplate(FIND).expand(gameName)
        fun foundByGameName(gameName: String) = UriTemplate(FOUND).expand(gameName)
        fun leaderboardByGameName(gameName: String) = UriTemplate(LEADERBOARD).expand(gameName)
        fun matchById(gameName: String, matchId: String) = UriTemplate(MATCH).expand(gameName, matchId)
        fun doTurnByGameName(gameName: String) = UriTemplate(DO_TURN).expand(gameName)
        fun isMyTurn(gameName: String, matchId: String) = UriTemplate(MY_TURN).expand(gameName, matchId)
        fun setupByGameName(gameName: String) = UriTemplate(SETUP).expand(gameName)
    }

    object User {
        const val REGISTER = "/user/register"
        const val LOGIN = "/user/login"
        const val LOGOUT = "/user/logout"

        const val ME = "/user/me"
        const val GET_BY_ID = "/user/{id}"
        const val UPDATE = "/user/update"
        const val DELETE = "/user/delete"

        fun byId(id: String) = UriTemplate(GET_BY_ID).expand(id)
    }
}
