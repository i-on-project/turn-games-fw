package pt.isel.tictactow

import com.fasterxml.jackson.databind.ObjectMapper
import pt.isel.fwinterfaces.Game
import pt.isel.fwinterfaces.GameLogic
import pt.isel.fwinterfaces.Match
import java.lang.IllegalArgumentException

class TicTacTowGameLogic: GameLogic {

    override fun create(users: List<Int>): Match {
        if (users.size != 2) {
            // TODO: Should throw error because list of players does not contain the right number
            throw IllegalArgumentException()
        }
        return Match(
            gameName = "TicTacTow",
            state = Match.State.ON_GOING,
            players = users,
            currPlayer = users.first(),
            currTurn = 1,
            info = ObjectMapper().valueToTree(Board.create())
        )
    }

    override fun setup(match: Match, infoSetup: GameLogic.InfoSetup): GameLogic.UpdateInfo {
        TODO("Not yet implemented")
    }

    override fun doTurn(match: Match, infoTurn: GameLogic.InfoTurn): GameLogic.UpdateInfo {
        return applyRound(match, infoTurn.playerId, ObjectMapper().treeToValue(infoTurn.info, Position::class.java))
    }

    override fun matchPlayerView(match: Match, playerId: Int): Match {
        return match
    }

    override fun getGameInfo(): Game = Game(
        "TicTacTow",
        2,
        "Description",
        "Rules"
    )

    private fun applyRound(
        match: Match,
        playerId: Int,
        position: Position,
    ): GameLogic.UpdateInfo {
        if (!match.players.contains(playerId)) { // No need to exist
            return GameLogic.UpdateInfo(true, "Player not in match", null)
        }
        return when (match.state) { // No need to exist
            Match.State.SETUP -> GameLogic.UpdateInfo(true, "Impossible to be on setup", null)
            Match.State.FINISHED -> GameLogic.UpdateInfo(false, "Game Already Ended", match)
            Match.State.ON_GOING -> {
                if (match.currPlayer != playerId) { // No need to exist
                    return GameLogic.UpdateInfo(true, "Not player Turn", match)
                }
                val playerNext: Int
                val playerState = if (match.currPlayer == match.players.first()) {
                    playerNext = 1
                    Board.State.PLAYER_X
                } else {
                    playerNext = 0
                    Board.State.PLAYER_O
                }
                val board: Board = ObjectMapper().treeToValue(match.info, Board::class.java)
                if (!board.canPlayOn(position)) {
                    return GameLogic.UpdateInfo(true, "Position not available!", null)
                }

                val newBoard = board.mutate(position, playerState)
                if (newBoard.hasWon(playerState)) {
                    val newMatch = match.copy(info = ObjectMapper().valueToTree(newBoard), state = Match.State.FINISHED)
                    return GameLogic.UpdateInfo(false, "Player ${playerState.char} won! Game Ended.", newMatch)
                }

                if (newBoard.isFull()) {
                    val newMatch = match.copy(
                        info = ObjectMapper().valueToTree(newBoard),
                        state = Match.State.FINISHED,
                    )
                    return GameLogic.UpdateInfo(
                        false,
                        "Draw! Game Ended.",
                        newMatch
                    )
                } else {
                    val newMatch = match.copy(
                        info = ObjectMapper().valueToTree(newBoard),
                        currPlayer = match.players[playerNext],
                        currTurn = match.currTurn + 1,
                    )
                    return GameLogic.UpdateInfo(
                        false,
                        "Next Player.",
                        newMatch
                    )
                }
            }
        }
    }
}
