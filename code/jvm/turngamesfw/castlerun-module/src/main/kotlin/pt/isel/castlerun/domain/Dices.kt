package pt.isel.castlerun.domain

/**
 * Represents a pair of dice used in the game.
 *
 * @property dice1 The value of the first dice.
 * @property dice2 The value of the second dice.
 */
data class Dices(var dice1: Int = 0, var dice2: Int = 0) {
    /**
     * Rolls both dice, setting their values to random numbers between 1 and 6.
     */
    fun roll() {
        dice1 = randomDiceValue()
        dice2 = randomDiceValue()
    }

    /**
     * Clears the values of both dice, resetting them to 0.
     */
    fun clear() {
        dice1 = 0
        dice2 = 0
    }

    /**
     * Calculates the sum of the values on both dice.
     *
     * @return The sum of the values on both dice.
     */
    fun sum(): Int = dice1 + dice2

    /**
     * Checks if the values on both dice are equal.
     *
     * @return `true` if the values on both dice are equal, `false` otherwise.
     */
    fun areEqual(): Boolean = dice1 == dice2

    /**
     * Retrieves the value of the smaller of the two dice.
     *
     * @return The value of the smaller dice.
     */
    fun smallest(): Int = if (dice1 < dice2) dice1 else dice2

    /**
     * Retrieves the value of the larger of the two dice.
     *
     * @return The value of the larger dice.
     */
    fun biggest(): Int = if (dice1 > dice2) dice1 else dice2

    // Private method to generate a random dice value between 1 and 6.
    private fun randomDiceValue(): Int = (1..6).random()
}
