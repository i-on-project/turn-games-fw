package pt.isel.turngamesfw.http.controller

import org.springframework.web.bind.annotation.*
import pt.isel.turngamesfw.http.Uris
@RestController
class UserController: BaseController() {
    @PostMapping(Uris.User.REGISTER)
    fun register(@RequestBody input: RegisterInputModel){
        TODO()
    }

    @PostMapping(Uris.User.LOGIN)
    fun login(@RequestBody input: LoginInputModel){
        TODO()
    }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(user: User){
        TODO()
    }

    @GetMapping(Uris.User.GET_BY_ID)
    fun getById(@PathVariable id: String){
        TODO()
    }

    @PutMapping(Uris.User.UPDATE)
    fun update(user: User){
        TODO()
    }

    @DeleteMapping(Uris.User.DELETE)
    fun delete(user: User){
        TODO()
    }

    @GetMapping(Uris.User.RANKING)
    fun ranking(){
        TODO()
    }
}