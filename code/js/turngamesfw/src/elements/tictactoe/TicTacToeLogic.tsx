type TicTacToeInfo = { cells: Array<Array<string>> }

export type TicTacToeMatch = {
    id: string;
    gameName: string;
    state: MatchState;
    players: number[];
    currPlayer: number;
    currTurn: number;
    deadlineTurn: Date;
    created: Date;
    info: TicTacToeInfo;
}

export const TicTacToeLogic = {
    newTurn: (match: TicTacToeMatch, row: number, col: number): TicTacToeMatch => {
        if (match.state == MatchState.FINISHED || !TicTacToeLogic.isValidMove(match.info.cells, row, col)) {
            return match;
        }

        const newMatch = TicTacToeLogic.makeMove(match, row, col);

        return newMatch;
    },

    isValidMove: (cells: Array<Array<string>>, row: number, col: number) => {
        return cells[row][col] === "EMPTY"
    },

    makeMove: (match: TicTacToeMatch, row: number, col: number): TicTacToeMatch => {
        match.info.cells[row][col] = TicTacToeLogic.playerSymbol(match.currTurn)

        const newInfo = match.info.cells
        newInfo[row][col] = TicTacToeLogic.playerSymbol(match.currTurn)

        let newCurrPlayer = TicTacToeLogic.changePlayer(match.currPlayer, match.players)
        let newCurrTurn = match.currTurn + 1

        let newState = match.state
        if (TicTacToeLogic.isGameOver(newInfo)) {
            newState = MatchState.FINISHED
            newCurrPlayer = match.currPlayer
            newCurrTurn = match.currTurn
        }

        return {
            ...match,
            state: newState,
            currPlayer: newCurrPlayer,
            currTurn: newCurrTurn,
            info: { cells: newInfo }
        }

    },

    changePlayer: (currentPlayer: number, players: number[]): number => {
        return players.find(id => currentPlayer != id)
    },

    playerSymbol: (currentTurn: number): string => {
        return currentTurn % 2 == 0 ? 'PLAYER_O' : 'PLAYER_X'
    },

    isGameOver: (cells: Array<Array<string>>): boolean => {        
        const winningConditions = [
            [{row: 0, col: 0}, {row: 0, col: 1}, {row: 0, col: 2}],
            [{row: 1, col: 0}, {row: 1, col: 1}, {row: 1, col: 2}],
            [{row: 2, col: 0}, {row: 2, col: 1}, {row: 2, col: 2}],
            [{row: 0, col: 0}, {row: 1, col: 0}, {row: 2, col: 0}],
            [{row: 0, col: 1}, {row: 1, col: 1}, {row: 2, col: 1}],
            [{row: 0, col: 2}, {row: 1, col: 2}, {row: 2, col: 2}],
            [{row: 0, col: 0}, {row: 1, col: 1}, {row: 2, col: 2}],
            [{row: 0, col: 2}, {row: 1, col: 1}, {row: 2, col: 0}],
        ];
        
        for (let i = 0; i < winningConditions.length; i++) {
            const [a, b, c] = winningConditions[i]
            if (
                cells[a.row][a.col] != "EMPTY" &&
                cells[a.row][a.col] === cells[b.row][b.col] &&
                cells[a.row][a.col] === cells[c.row][c.col]
            ) {
                return true
            }
        }

        if (cells.every(row => row.every(col => col != "EMPTY"))) {
            return true
        }

        return false
    },
    
}