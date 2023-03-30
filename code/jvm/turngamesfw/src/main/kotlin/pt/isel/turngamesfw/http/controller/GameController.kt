package pt.isel.turngamesfw.http.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.SetupInputModel
import pt.isel.turngamesfw.http.model.TurnInputModel
import pt.isel.turngamesfw.services.GameServices

@RestController
class GameController(
    gameServices: GameServices
) {

    @GetMapping(Uris.Game.GAME_HOME)
    fun home(user: User, @PathVariable nameGame: String){
        TODO()
    }

    @PostMapping(Uris.Game.FIND)
    fun findMatch(user: User, @PathVariable nameGame: String){
        TODO()
    }

    @GetMapping(Uris.Game.FOUND)
    fun foundMatch(user: User, @PathVariable nameGame: String){
        TODO()
    }

    @GetMapping(Uris.Game.GET_BY_ID)
    fun getMatchById(user: User, @PathVariable nameGame: String, @PathVariable id: String){
        TODO()
    }

    @PostMapping(Uris.Game.SETUP)
    fun setup(user: User, @PathVariable nameGame: String, @RequestBody setup: SetupInputModel){
        TODO()
    }

    @PostMapping(Uris.Game.DO_TURN)
    fun doTurn(user: User, @PathVariable nameGame: String, @RequestBody turn: TurnInputModel){
        TODO()
    }
}