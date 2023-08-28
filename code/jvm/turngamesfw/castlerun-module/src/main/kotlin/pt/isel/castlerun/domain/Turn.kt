package pt.isel.castlerun.domain

data class Turn(val type: String, val move: Move?, val duel: Duel?)

/**
 * Represents a duel between two game pieces.
 *
 * @property ally The player's own piece in the duel.
 * @property enemy The enemy piece in the duel.
 * @property duelDices The dice values rolled for the duel.
 * @property duelNumber The duel number used to identify what type of duel will be executed.
 */
data class Duel(val ally: Piece, val enemy: Piece, val duelDices: Dices, val duelNumber: Int)

/**
 * Represents a move of a game piece.
 *
 * @property piece The piece being moved.
 * @property to The target coordinates of the move.
 */
data class Move(val piece: Piece?, val to: Coords)
