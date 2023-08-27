import { ShipModel } from "./typesGame"

type BattleshipInfo = {
    myBoard: {
        boardShips: {
            grid: Array<Array<boolean>>,
            ships: Array<ShipModel>
        },
        boardShots: {
            grid: Array<Array<boolean>> 
        }
    },
    myShots: {
        grid: Array<Array<boolean>> 
    },
}

export type BattleshipMatch = {
    id: string;
    gameName: string;
    state: MatchState;
    players: number[];
    currPlayer: number;
    currTurn: number;
    deadlineTurn: Date;
    created: Date;
    info: BattleshipInfo;
}

export const BattleshipLogic = {
    setup: (match: BattleshipMatch, fleet: Array<ShipModel>): BattleshipMatch => {
        return {
            ...match,
            info: {
                ...match.info,
                myBoard: {
                    ...match.info.myBoard,
                    boardShips: {
                        ...match.info.myBoard.boardShips,
                        ships: fleet
                    }
                }
            }
        }
    },

    newTurn: (match: BattleshipMatch, row: number, col: number): BattleshipMatch => {
        if (match.state == MatchState.FINISHED || !BattleshipLogic.isValidMove(match.info.myShots.grid, row, col)) {
            return match;
        }

        const newMatch = BattleshipLogic.makeMove(match, row, col);

        return newMatch;
    },

    isValidMove: (cells: Array<Array<boolean>> , row: number, col: number) => {
        return cells[row][col] === false
    },

    makeMove: (match: BattleshipMatch, row: number, col: number): BattleshipMatch => {
        const newInfo = match.info.myShots.grid
        newInfo[row][col] = true

        let newCurrPlayer = BattleshipLogic.changePlayer(match.currPlayer, match.players)
        let newCurrTurn = match.currTurn + 1

        let newState = match.state
        return {
            ...match,
            state: newState,
            currPlayer: newCurrPlayer,
            currTurn: newCurrTurn,
            info: {
                ...match.info,
                myShots: {grid: newInfo}
            }
        }
    },

    changePlayer: (currentPlayer: number, players: number[]): number => {
        return players.find(id => currentPlayer != id)
    },
}