package pt.isel.domain

data class Dices(var dice1: Int = 0, var dice2: Int = 0) {
    fun roll() { dice1 = randomDiceValue(); dice2 = randomDiceValue() }
    fun clear() { dice1 = 0; dice2 = 0 }
    fun sum() = dice1 + dice2

    fun areEqual () = dice1 == dice2

    fun smallest() = if (dice1 < dice2) dice1 else dice2
    fun biggest() = if (dice1 > dice2) dice1 else dice2

    private fun randomDiceValue() = (1..6).random()
}