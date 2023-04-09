package pt.isel.turngamesfw.repository

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.repository.jdbi.JdbiUserRepository
import pt.isel.turngamesfw.utils.testWithHandleAndRollback
import kotlin.test.assertEquals

class UserRepositoryTests {

    @Test
    fun `can create and retrieve user`(): Unit = testWithHandleAndRollback { handle ->

        // Repository
        val userRepo = JdbiUserRepository(handle)

        // Store user in repository
        val idPlayer = userRepo.createUser("playerTest", User.PasswordValidationInfo("123"))

        // Check if exist user by username
        assertEquals(true, userRepo.isUserStoredByUsername("playerTest"))

        // Get user inserted by username
        var player = userRepo.getUserByUsername("playerTest") ?: fail("user must exist")

        // Check if player as the correct username
        assertEquals("playerTest", player.username)

        // Get user inserted by id
        player = userRepo.getUserById(idPlayer) ?: fail("user must exist")

        // Check if player as the correct username
        assertEquals("playerTest", player.username)

    }

}