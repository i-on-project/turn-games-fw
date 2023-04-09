package pt.isel.turngamesfw.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.turngamesfw.repository.GameRepository
import pt.isel.turngamesfw.repository.Transaction
import pt.isel.turngamesfw.repository.UserRepository

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val usersRepository: UserRepository by lazy { JdbiUserRepository(handle) }

    override val gamesRepository: GameRepository by lazy { JdbiGameRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }
}