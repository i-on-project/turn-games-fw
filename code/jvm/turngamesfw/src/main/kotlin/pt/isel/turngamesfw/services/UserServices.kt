package pt.isel.turngamesfw.services

import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.User

@Component
class UserServices {

    fun createUser(username: String, password: String) {
        TODO()
    }

    fun createToken(username: String, password: String) {
        TODO()
    }

    fun getUserByToken(token: String): User? {
        TODO()
    }

    fun getUserById(id: Int): User? {
        TODO()
    }

    fun updateUser(user: User): User {
        TODO()
    }

    fun deleteUser(id: Int) {
        TODO()
    }

}
