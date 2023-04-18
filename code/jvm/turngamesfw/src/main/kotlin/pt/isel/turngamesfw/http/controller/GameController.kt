package pt.isel.turngamesfw.http.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.turngamesfw.domain.GameLogic
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.*
import pt.isel.turngamesfw.services.GameServices
import pt.isel.turngamesfw.services.results.*
import pt.isel.turngamesfw.utils.Either
import java.lang.IllegalArgumentException
import java.util.*

@RestController
class GameController(
    private val gameServices: GameServices,
) {

    @GetMapping(Uris.Game.GAME_INFO)
    fun getGameInfo(user: User, @PathVariable nameGame: String): ResponseEntity<*> {
        val game = gameServices.getGameInfo(nameGame) ?: return problemResponse(Problem.GAME_NOT_EXIST)

        return ResponseEntity.status(200).body(GameOutputModel.fromGame(game))
    }

    @GetMapping(Uris.Game.GAME_LEADERBOARD)
    fun getLeaderboardByName(user: User, @RequestBody leaderboard: LeaderBoardInputModel) =
        getLeaderboard(user, leaderboard.gameName, leaderboard)

    @GetMapping(Uris.Game.LEADERBOARD)
    fun getLeaderboard(user: User, @PathVariable nameGame: String, @RequestBody leaderboard: LeaderBoardInputModel){
        TODO()
    }

    @PostMapping(Uris.Game.GAME_FIND)
    fun findMatchByName(user: User, @RequestBody gameName: GameNameInputModel) =
        findMatch(user, gameName.gameName)

    @PostMapping(Uris.Game.FIND)
    fun findMatch(user: User, @PathVariable nameGame: String): ResponseEntity<*> {
        return when (val res = gameServices.findMatch(nameGame, user.id)) {
            is Either.Left -> when (res.value) {
                FindMatchError.AlreadySearchingOrInGame -> problemResponse(Problem.USER_ALREADY_SEARCHING_IN_GAME)
                FindMatchError.GameNotExist -> problemResponse(Problem.GAME_NOT_EXIST)
            }
            is Either.Right -> ResponseEntity.status(303)
                .header("Location", Uris.Game.FOUND)
                .build<Unit>()
        }
    }

    @GetMapping(Uris.Game.FOUND)
    fun foundMatch(user: User, @PathVariable nameGame: String): ResponseEntity<*> {
        return when (val res = gameServices.foundMatch(nameGame, user.id)) {
            is Either.Left -> when (res.value) {
                FoundMatchError.ServerError -> problemResponse(Problem.SERVER_ERROR)
                FoundMatchError.UserNotFound -> problemResponse(Problem.USER_NOT_FOUND)
            }
            is Either.Right -> ResponseEntity.status(200).body(MatchOutputModel.fromMatch(res.value))
        }
    }

    @GetMapping(Uris.Game.GET_BY_ID)
    fun getMatchById(user: User, @PathVariable nameGame: String, @PathVariable id: String): ResponseEntity<*> {
        val matchId = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return problemResponse(Problem.INVALID_MATCH_ID)
        }

        return when (val res = gameServices.getMatchById(matchId, user.id)) {
            is Either.Left -> when (res.value) {
                MatchByIdError.MatchNotExist -> problemResponse(Problem.MATCH_NOT_EXIST)
                MatchByIdError.ServerError -> problemResponse(Problem.SERVER_ERROR)
            }
            is Either.Right -> ResponseEntity.status(200).body(MatchOutputModel.fromMatch(res.value))
        }
    }

    @PostMapping(Uris.Game.SETUP)
    fun setup(user: User, @PathVariable nameGame: String, @RequestBody setup: SetupInputModel): ResponseEntity<*> {
        return when (val res = gameServices.setup(setup.matchId, GameLogic.InfoSetup(user.id, setup.info))) {
            is Either.Left -> when (res.value) {
                SetupMatchError.MatchNotExist -> problemResponse(Problem.MATCH_NOT_EXIST)
                SetupMatchError.ServerError -> problemResponse(Problem.SERVER_ERROR)
                SetupMatchError.UserNotInMatch -> problemResponse(Problem.USER_NOT_IN_MATCH)
            }
            is Either.Right -> when (val r = res.value) {
                is SetupMatchSuccess.SetupDone -> ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(r.resp)
            }
        }
    }

    @PostMapping(Uris.Game.DO_TURN)
    fun doTurn(user: User, @PathVariable nameGame: String, @RequestBody turn: TurnInputModel): ResponseEntity<*> {
        return when (val res = gameServices.doTurn(turn.matchId, GameLogic.InfoTurn(user.id, turn.info))) {
            is Either.Left -> when (res.value) {
                DoTurnMatchError.MatchNotExist -> problemResponse(Problem.MATCH_NOT_EXIST)
                DoTurnMatchError.ServerError -> problemResponse(Problem.SERVER_ERROR)
                DoTurnMatchError.UserNotInMatch -> problemResponse(Problem.USER_NOT_IN_MATCH)
            }
            is Either.Right -> when (val r = res.value) {
                is DoTurnMatchSuccess.DoTurnDone -> ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(r.resp)
            }
        }
    }
}