package pt.isel.turngamesfw.http.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.*
import pt.isel.turngamesfw.services.UserServices
import pt.isel.turngamesfw.services.results.TokenCreationError
import pt.isel.turngamesfw.services.results.UserCreationError
import pt.isel.turngamesfw.utils.Either
import java.util.*


@RestController
class UserController(
    private val userServices: UserServices
) {
    @PostMapping(Uris.User.REGISTER)
    fun register(@RequestBody input: RegisterInputModel): ResponseEntity<*> =
        when (val res = userServices.createUser(input.username, input.password)) {
            is Either.Left -> when (res.value) {
                UserCreationError.InvalidArguments -> problemResponse(Problem.INVALID_ARGUMENTS)
                UserCreationError.UserAlreadyExists -> problemResponse(Problem.USER_ALREADY_EXISTS)
            }
            is Either.Right -> SirenPages.register(res.value.toUserDetailsOutputModel()).toResponseEntity(status = HttpStatus.CREATED) {  }
        }

    @GetMapping(Uris.User.LOGIN)
    fun loginPge(@RequestBody input: LoginInputModel): ResponseEntity<*> =
        SirenPages.login(null).toResponseEntity { }

    @PostMapping(Uris.User.LOGIN)
    fun login(@RequestBody input: LoginInputModel): ResponseEntity<*> =
        when (val res = userServices.createToken(input.username, input.password)) {
            is Either.Left -> when (res.value) {
                TokenCreationError.InvalidArguments -> problemResponse(Problem.INVALID_LOGIN)
                TokenCreationError.UserOrPasswordAreInvalid -> problemResponse(Problem.INVALID_LOGIN)
            }
            is Either.Right -> {
                val headers = mutableMapOf(Pair("Set-Cookie", "TGFW-Cookie=${res.value}; SameSite=Strict"))
                SirenPages.login(UserTokenOutputModel(res.value)).toResponseEntity(headers = headers) {}
            }
        }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(user: User): ResponseEntity<*> {
        userServices.updateStatus(user.id, User.Status.OFFLINE)
        return SirenPages.home(null, null, emptyList()).toResponseEntity {  }
        //TODO add the game list
    }

    @GetMapping(Uris.User.GET_BY_ID)
    fun getById(@PathVariable id: String): ResponseEntity<*> {
        val userId = try {
            id.toInt()
        } catch (e: IllegalArgumentException) {
            return problemResponse(Problem.USER_NOT_FOUND)
        }

        val user = userServices.getUserById(userId)?: return problemResponse(Problem.USER_NOT_FOUND)
        return SirenPages.user(user.toUserDetailsOutputModel()).toResponseEntity {  }
    }

    //TODO create the update and delete user services

    /*
    @PutMapping(Uris.User.UPDATE)
    fun update(user: User, @RequestBody input: UpdateUserInputModel): ResponseEntity<*> = 
        when (val res = userServices.updateUser(user.id, input.username)) {
            is Either.Left -> when (res.value) {
                UserServices.UpdateUserError.InvalidArguments -> problemResponse(Problem.INVALID_UPDATE)
                UserServices.UpdateUserError.UserAlreadyExists -> problemResponse(Problem.USER_ALREADY_EXISTS)
            }
            is Either.Right -> ResponseEntity.status(200).build<Unit>()
    }

    @DeleteMapping(Uris.User.DELETE)
    fun delete(user: User): ResponseEntity<*> = 
        when (val res = userServices.deleteUser(user.id)) {
            is Either.Left -> when (res.value) {
                UserServices.DeleteUserError.ServerError -> problemResponse(Problem.SERVER_ERROR)
                UserServices.DeleteUserError.UserNotFound -> problemResponse(Problem.USER_NOT_FOUND)
            }
            is Either.Right -> ResponseEntity.status(200).build<Unit>()
    }
     */
}