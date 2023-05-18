package pt.isel.turngamesfw.utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.turngamesfw.repository.GameRepository
import pt.isel.turngamesfw.repository.Transaction
import pt.isel.turngamesfw.repository.TransactionManager
import pt.isel.turngamesfw.repository.UserRepository
import pt.isel.turngamesfw.repository.jdbi.JdbiTransaction
import pt.isel.turngamesfw.repository.jdbi.configure

private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL("jdbc:postgresql://localhost:5432/dbTurnGamesFW?user=dbuser&password=12345")
    }
).configure()

fun testWithHandleAndRollback(block: (Handle) -> Unit) = jdbi.useTransaction<Exception> { handle ->
    block(handle)
    handle.rollback()
}

fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) = jdbi.useTransaction<Exception>
{ handle ->

    val transaction = JdbiTransaction(handle)

    // Test TransactionManager that never commits
    val transactionManager = object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R {
            return block(transaction)
            // n.b. no commit happens
        }
    }
    block(transactionManager)

    // Finally, we roll back everything
    handle.rollback()
}


/***
 * Return transaction manager with repositories on arguments
 * Used for Mock Repositories
 */
fun getTransactionManager(userRepository: UserRepository, gameRepository: GameRepository): TransactionManager {
    val transaction = object : Transaction {
        override val usersRepository: UserRepository = userRepository
        override val gamesRepository: GameRepository = gameRepository

        override fun rollback() {
            return
        }
    }
    return object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R {
            return block(transaction)
        }
    }
}