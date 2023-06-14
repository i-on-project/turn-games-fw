package pt.isel.turngamesfw.http.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.fwinterfaces.GameLogic
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

    @GetMapping(Uris.Game.ALL_GAMES)
    fun getGameList(): ResponseEntity<*> {
        val gameList = gameServices.getGameList()

        return SirenPages.gameList(GameListOutputModel(gameList)).toResponseEntity {  }
    }

    @GetMapping(Uris.Game.INFO)
    fun getGameInfo(@PathVariable nameGame: String): ResponseEntity<*> {
        val game = gameServices.getGameInfo(nameGame) ?: return problemResponse(Problem.GAME_NOT_EXIST)

        return SirenPages.gameInfo(GameOutputModel.fromGame(game)).toResponseEntity {  }
    }

    @GetMapping(Uris.Game.LEADERBOARD)
    fun getLeaderboard(user: User, @PathVariable nameGame: String, @RequestParam page: Int, @RequestParam limit: Int){
        TODO()
    }

    @GetMapping(Uris.Game.MY_STATE)
    fun myState(user: User, @PathVariable nameGame: String): ResponseEntity<*> {
        val state = gameServices.getState(nameGame, user.id)
        return SirenPages.myState(MyStateOutputModel(state)).toResponseEntity {  }
    }

    @PostMapping(Uris.Game.FIND)
    fun findMatch(user: User, @PathVariable nameGame: String): ResponseEntity<*> =
        when (val res = gameServices.findMatch(nameGame, user.id)) {
            is Either.Left -> when (res.value) {
                FindMatchError.AlreadySearchingOrInGame -> problemResponse(Problem.USER_ALREADY_SEARCHING_IN_GAME)
                FindMatchError.GameNotExist -> problemResponse(Problem.GAME_NOT_EXIST)
            }
            is Either.Right -> {
                val headers = HttpHeaders()
                headers.add("Location", Uris.Game.foundByGameName(nameGame).toString())
                SirenPages.empty().toResponseEntity(status = HttpStatus.SEE_OTHER, headers = headers) {  }
            }
        }

    @GetMapping(Uris.Game.FOUND)
    fun foundMatch(user: User, @PathVariable nameGame: String): ResponseEntity<*> =
        when (val res = gameServices.foundMatch(nameGame, user.id)) {
            is Either.Left -> when (res.value) {
                FoundMatchError.ServerError -> problemResponse(Problem.SERVER_ERROR)
                FoundMatchError.UserNotInGame -> problemResponse(Problem.USER_NOT_IN_MATCH)
            }
            is Either.Right -> when (val r = res.value) {
                is FoundMatchSuccess.FoundMatch -> SirenPages.foundMatch(FoundMatchOutputModel(true, MatchOutputModel.fromMatch(r.match))).toResponseEntity {  }
                FoundMatchSuccess.SearchingMatch -> SirenPages.foundMatch(FoundMatchOutputModel(false, null)).toResponseEntity { }
            }
        }

    @GetMapping(Uris.Game.MATCH)
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
            is Either.Right -> SirenPages.match(MatchOutputModel.fromMatch(res.value)).toResponseEntity {  }
        }
    }

    @PostMapping(Uris.Game.SETUP)
    fun setup(user: User, @PathVariable nameGame: String, @RequestBody setup: SetupInputModel): ResponseEntity<*> {
        return when (val res = gameServices.setup(setup.matchId, GameLogic.InfoSetup(user.id, setup.info))) {
            is Either.Left -> when (res.value) {
                SetupMatchError.MatchNotExist -> problemResponse(Problem.MATCH_NOT_EXIST)
                SetupMatchError.ServerError -> problemResponse(Problem.SERVER_ERROR)
                SetupMatchError.UserNotInMatch -> problemResponse(Problem.USER_NOT_IN_MATCH)
                SetupMatchError.MatchStateError -> problemResponse(Problem.MATCH_STATE)
            }
            is Either.Right -> when (val r = res.value) {
                is SetupMatchSuccess.SetupDone -> ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(r.resp)
                is SetupMatchSuccess.ErrorInGameLogic -> ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(r.resp)
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
                DoTurnMatchError.MatchStateError -> problemResponse(Problem.MATCH_STATE)
                DoTurnMatchError.NotYourTurn -> problemResponse(Problem.NOT_YOUR_TURN)
            }
            is Either.Right -> when (val r = res.value) {
                is DoTurnMatchSuccess.DoTurnDone -> ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(r.resp)
                is DoTurnMatchSuccess.ErrorInGameLogic -> ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(r.resp)
            }
        }
    }

    @GetMapping(Uris.Game.MY_TURN)
    fun isMyTurn(user: User, @PathVariable nameGame: String, @PathVariable id: String): ResponseEntity<*> {
        return when (val res = gameServices.isMyTurn(user.id, UUID.fromString(id))) {
            is Either.Left -> when (res.value) {
                MyTurnError.MatchNotExist -> problemResponse(Problem.MATCH_NOT_EXIST)
                MyTurnError.ServerError -> problemResponse(Problem.SERVER_ERROR)
                MyTurnError.UserNotInMatch -> problemResponse(Problem.USER_NOT_IN_MATCH)
            }
            is Either.Right -> ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(res.value)
        }
    }
}