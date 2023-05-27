class TicTacToeGame {
    squares: string[];
    currentPlayer: string;
    gameOver: boolean;
}

export const TicTacToeMatch: Match = {
    id: '1',
    gameName: 'TicTacToe',
    state: MatchState.ON_GOING,
    currTurn: 1,
    currPlayer: 0,
    players: [0, 1],
    deadlineTurn: new Date(Date.now() + 1000 * 60 * 15),
    created: new Date(),
    info: {
      squares: Array(9).fill(null),
      currentPlayer: 'X',
      gameOver: false,
    }
  };

export const TicTacToeLogic = {
    newTurn: (game: TicTacToeGame, index: number): TicTacToeGame => {
        if (game.gameOver || !TicTacToeLogic.isValidMove(game.squares, index)) {
            return game;
        }

        const newSquares = TicTacToeLogic.makeMove(game.squares, index, game.currentPlayer);
        const newPlayer = TicTacToeLogic.changePlayer(game.currentPlayer);
        const gameOver = TicTacToeLogic.isGameOver(newSquares);

        return {
            squares: newSquares,
            currentPlayer: newPlayer,
            gameOver: gameOver,
        };
    },

    isValidMove: (squares: string[], index: number) => {
        return squares[index] === null;
    },

    makeMove: (squares: string[], index: number, currentPlayer: string): string[] => {
        const newSquares = [...squares];
        newSquares[index] = currentPlayer;
        return newSquares;
    },

    changePlayer: (currentPlayer: string): string => {
        return currentPlayer === 'X' ? 'O' : 'X';
    },

    isGameOver: (squares: string[]): boolean => {        
        const winningConditions = [
            [0, 1, 2],
            [3, 4, 5],
            [6, 7, 8],
            [0, 3, 6],
            [1, 4, 7],
            [2, 5, 8],
            [0, 4, 8],
            [2, 4, 6],
        ];
        
        for (let i = 0; i < winningConditions.length; i++) {
            const [a, b, c] = winningConditions[i];
            if (
                squares[a] &&
                squares[a] === squares[b] &&
                squares[a] === squares[c]
            ) {
                console.log('Game Over by win');
                return true;
            }
        }

        if (squares.every((square) => square !== null)) {
            console.log('Game Over by draw');
            return true;
        }

        
        return false;
    },
    
    resetGame: () => {
        return {
            squares: Array(9).fill(null),
            currentPlayer: 'X',
            gameOver: false
        };
    },
}