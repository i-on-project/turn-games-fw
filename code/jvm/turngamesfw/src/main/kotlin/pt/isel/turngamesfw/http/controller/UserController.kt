package pt.isel.turngamesfw.http.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.http.Uris
import pt.isel.turngamesfw.http.model.*
import pt.isel.turngamesfw.services.UserServices
import pt.isel.turngamesfw.services.results.TokenCreationError
import pt.isel.turngamesfw.services.results.UserCreationError
import pt.isel.turngamesfw.utils.Either
import java.time.Duration
import java.util.*

@RestController
class UserController(
    private val userServices: UserServices
) {
    private val cookieName = "TGFWCookie"

    @GetMapping(Uris.User.REGISTER)
    fun registerPage(@RequestBody input: LoginInputModel): ResponseEntity<*> =
        SirenPages.register().toResponseEntity { }

    @PostMapping(Uris.User.REGISTER)
    fun register(@RequestBody input: RegisterInputModel): ResponseEntity<*> =
        when (val res = userServices.createUser(input.username, input.password)) {
            is Either.Left -> when (res.value) {
                UserCreationError.InvalidArguments -> problemResponse(Problem.INVALID_ARGUMENTS)
                UserCreationError.UserAlreadyExists -> problemResponse(Problem.USER_ALREADY_EXISTS)
                UserCreationError.ServerError -> problemResponse(Problem.SERVER_ERROR)
            }
            is Either.Right -> SirenPages.empty().toResponseEntity(status = HttpStatus.CREATED) {  }
        }

    @GetMapping(Uris.User.LOGIN)
    fun loginPage(@RequestBody input: LoginInputModel): ResponseEntity<*> =
        SirenPages.login().toResponseEntity { }

    private fun createLoginCookie(token: String) =
        ResponseCookie.from(cookieName, token).httpOnly(true).secure(true).path("/").domain("localhost").maxAge(Duration.ofHours(1)).sameSite("Lax").build()

    @PostMapping(Uris.User.LOGIN)
    fun login(@RequestBody input: LoginInputModel): ResponseEntity<*> =
        when (val res = userServices.createToken(input.username, input.password)) {
            is Either.Left -> when (res.value) {
                TokenCreationError.InvalidArguments -> problemResponse(Problem.INVALID_LOGIN)
                TokenCreationError.UserOrPasswordAreInvalid -> problemResponse(Problem.INVALID_LOGIN)
            }
            is Either.Right -> {
                val cookie = createLoginCookie(res.value)

                val headers = HttpHeaders()
                    headers.add(HttpHeaders.SET_COOKIE, cookie.toString())
                SirenPages.empty().toResponseEntity(headers = headers) {}
            }
        }

    private val logoutCookie = ResponseCookie.from(cookieName, "").httpOnly(true).secure(true).path("/").domain("localhost").maxAge(Duration.ZERO).sameSite("Lax").build()

    @PostMapping(Uris.User.LOGOUT)
    fun logout(user: User): ResponseEntity<*> {
        userServices.updateStatus(user.id, User.Status.OFFLINE)

        val headers = HttpHeaders()
        headers.add(HttpHeaders.SET_COOKIE, logoutCookie.toString())

        return SirenPages.empty().toResponseEntity(headers = headers) {  }
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
}