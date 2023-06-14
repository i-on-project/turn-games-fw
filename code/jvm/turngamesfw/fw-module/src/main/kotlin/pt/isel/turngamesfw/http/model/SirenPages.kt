package pt.isel.turngamesfw.http.model

import org.springframework.http.HttpMethod
import pt.isel.turngamesfw.domain.LeaderboardPageAndLimit
import pt.isel.turngamesfw.domain.LeaderboardUser
import pt.isel.fwinterfaces.Match
import pt.isel.turngamesfw.http.Rels
import pt.isel.turngamesfw.http.Uris
import java.net.URI

class SirenPages {
    companion object {
        fun empty() = siren(Unit) {}

        fun home(userId: Int? = null, token: String?, gameNameList: List<String>) = siren(Unit) {
            clazz("home")

            gameNameList.forEach { gameName ->
                entity(gameName, Rels.ITEM) {
                    linkSelf(Uris.Game.infoByGameName(gameName))

                    action("start-game",  Uris.Game.findByGameName(gameName), HttpMethod.POST) {}
                    action("get-leaderboard", Uris.Game.leaderboardByGameName(gameName), HttpMethod.GET) {}
                    action("get-info", Uris.Game.infoByGameName(gameName), HttpMethod.GET) {}
                }
            }

            if (token != null)
                action("logout", URI(Uris.User.LOGOUT), HttpMethod.POST, "application/json") {}

            linkSelf(Uris.HOME)

            link(Uris.User.LOGIN, Rels.LOGIN)
            link(Uris.User.REGISTER, Rels.REGISTER)
            link(Uris.ABOUT, Rels.ABOUT)
            if (userId != null)
                link(Uris.User.byId(userId.toString()), Rels.ME)
        }

        fun login() = siren(Unit) {
            clazz("login")

            action("login", URI(Uris.User.LOGIN), HttpMethod.POST, "application/json") {
                textField("username")
                passwordField("password")
            }

            linkSelf(Uris.User.LOGIN)

            link(Uris.HOME, Rels.HOME)
            link(Uris.User.REGISTER, Rels.REGISTER)
        }

        fun register() = siren(Unit) {
            clazz("register")

            action("register", URI(Uris.User.REGISTER), HttpMethod.POST, "application/json") {
                textField("username")
                passwordField("password")
            }

            linkSelf(Uris.User.REGISTER)

            link(Uris.HOME, Rels.HOME)
            link(Uris.User.LOGIN, Rels.LOGIN)
        }

        fun about() = siren(Unit) {
            clazz("about")

            //TODO missing properties

            linkSelf(Uris.ABOUT)
            link(Uris.HOME, Rels.HOME)
        }

        fun user(user: UserDetailsOutputModel) = siren(user) {
            clazz("user")

            linkSelf(Uris.User.byId(user.id.toString()))
            link(Uris.HOME, Rels.HOME)
        }

        fun gameList(gameList: GameListOutputModel) = siren(gameList) {
            clazz("gameList")

            linkSelf(Uris.Game.ALL_GAMES)

            link(Uris.HOME, Rels.HOME)
        }

        fun gameInfo(game: GameOutputModel) = siren(game) {
            clazz("game")

            linkSelf(Uris.Game.infoByGameName(game.name))

            link(Uris.HOME, Rels.HOME)
        }

        fun leaderboard(leaderboard: LeaderboardPageAndLimit, list: List<LeaderboardUser>) = siren(leaderboard) {
            clazz("leaderboard")

            list.forEach { user ->
                entity(user, Rels.ITEM) {
                    linkSelf(Uris.User.byId(user.id.toString()))
                }
            }

            linkSelf(Uris.Game.LEADERBOARD)

            link(Uris.Game.LEADERBOARD, Rels.NEXT)
            link(Uris.Game.LEADERBOARD, Rels.PREVIOUS)
        }

        fun myState(myState: MyStateOutputModel) = siren(myState) {
            clazz("myState")

            // TODO: Add remaining links
        }

        fun foundMatch(foundMatch: FoundMatchOutputModel) = siren(foundMatch) {
            clazz("foundMatch")

            // TODO: Add remaining links
        }

        fun match(match: MatchOutputModel) = siren(match) {
            clazz("match")

            match.players.forEach { playerId ->
                entity(playerId, Rels.ITEM) {
                    linkSelf(Uris.User.byId(playerId.toString()))
                }
            }

            if (match.state == Match.State.SETUP)
                action("setup", Uris.Game.setupByGameName(match.gameName), HttpMethod.POST, "application/json") {
                    textField("matchId")
                    textField("info")
                }

            if (match.state == Match.State.ON_GOING)
                action("setup", Uris.Game.doTurnByGameName(match.gameName), HttpMethod.POST,"application/json") {
                    textField("matchId")
                    textField("info")
                }

            linkSelf(Uris.Game.matchById(match.gameName, match.id.toString()))

            link(Uris.HOME, Rels.HOME)
            link(Uris.Game.infoByGameName(match.gameName), Rels.GAMES)
        }
    }
}