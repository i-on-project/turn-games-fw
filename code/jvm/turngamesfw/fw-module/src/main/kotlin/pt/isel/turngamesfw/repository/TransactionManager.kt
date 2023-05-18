package pt.isel.turngamesfw.repository

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}