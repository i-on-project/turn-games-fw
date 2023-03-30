package pt.isel.turngamesfw.http.controller

import org.springframework.web.bind.annotation.*
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.LoginInputModel
import pt.isel.turngamesfw.http.model.RegisterInputModel
import pt.isel.turngamesfw.http.model.UpdateUserInputModel
import pt.isel.turngamesfw.services.UserServices

@RestController
class UserController(
    private val userServices: UserServices
) {
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
    fun update(user: User, @RequestBody input: UpdateUserInputModel){
        TODO()
    }

    @DeleteMapping(Uris.User.DELETE)
    fun delete(user: User){
        TODO()
    }

}