package pt.isel.turngamesfw.repository

interface Transaction {
    val usersRepository: UserRepository
    val gamesRepository: GameRepository
    fun rollback()
}