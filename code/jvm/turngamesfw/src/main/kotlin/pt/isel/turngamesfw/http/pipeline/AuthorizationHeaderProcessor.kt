package pt.isel.turngamesfw.http.pipeline

import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.services.UserServices

@Component
class AuthorizationHeaderProcessor(
    val usersService: UserServices
) {

    fun process(authorizationValue: String?): User? {
        if (authorizationValue == null) {
            return null
        }
        return usersService.getUserByToken(authorizationValue)
    }

    companion object {
        const val SCHEME = "bearer"
    }
}