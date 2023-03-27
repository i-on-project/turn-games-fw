package pt.isel.turngamesfw.http.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.turngamesfw.http.Uris

@RestController
class GameController: BaseController() {
    @PostMapping(Uris.Game.SEARCH)
    fun search(user: User){
        TODO()
    }

    @GetMapping(Uris.Game.FOUND)
    fun found(user: User){
        TODO()
    }

    @GetMapping(Uris.Game.GET_BY_ID)
    fun getById(user: User, @PathVariable id: String){
        TODO()
    }

    @PostMapping(Uris.Game.SETUP)
    fun setup(user: User, @RequestBody setup: SetupInputModel){
        TODO()
    }

    @PostMapping(Uris.Game.DO_TURN)
    fun doTurn(user: User, @RequestBody turn: TurnInputModel){
        TODO()
    }
}