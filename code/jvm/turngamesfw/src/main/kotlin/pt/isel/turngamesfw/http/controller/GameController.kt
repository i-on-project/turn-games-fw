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

@RestController
class GameController {

    @GetMapping(Uris.Game.HOME)
    fun home(user: User, @PathVariable game: String){
        TODO()
    }

    @PostMapping(Uris.Game.SEARCH)
    fun search(user: User, @PathVariable game: String){
        TODO()
    }

    @GetMapping(Uris.Game.FOUND)
    fun found(user: User, @PathVariable game: String){
        TODO()
    }

    @GetMapping(Uris.Game.GET_BY_ID)
    fun getById(user: User, @PathVariable game: String, @PathVariable id: String){
        TODO()
    }

    @PostMapping(Uris.Game.SETUP)
    fun setup(user: User, @PathVariable game: String, @RequestBody setup: SetupInputModel){
        TODO()
    }

    @PostMapping(Uris.Game.DO_TURN)
    fun doTurn(user: User, @PathVariable game: String, @RequestBody turn: TurnInputModel){
        TODO()
    }
}