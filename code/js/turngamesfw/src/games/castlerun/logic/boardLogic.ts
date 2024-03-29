import { Duel, Move } from "../CastleRunMatch";
import { Board } from "../domain/Board";
import { Coords } from "../domain/Coords";
import { Piece } from "../domain/Piece";
import { Tile } from "../domain/Tile";

export class BoardLogic {
    static getTile(board: Board, c: Coords): Tile {
        return board.tiles[c.row * board.numCols + c.col];
    }

    static getExits(board: Board): Tile[] {
        return board.tiles.filter(tile => tile.type === Tile.Type.Exit);
    }

    static isWithinBorders(board: Board, c: Coords): boolean {
        return c.row >= 0 && c.row < board.numRows && c.col >= 0 && c.col < board.numCols;
    }

    static updateTile(board: Board, newTile: Tile): Board {
        const updatedTiles = board.tiles.map(tile => {
            if (tile.coords == newTile.coords) {
                return newTile;
            }
            return tile;
        });
        return new Board(board.alpha, board.beta, board.numRows, board.numCols, board.numPieces, board.piecesLeft, updatedTiles);
    }
    
    static isGameOver(board: Board): boolean {
        const exits = this.getExits(board);
        const playerAScore = exits.filter(tile => tile.piece?.owner === board.alpha).length;
        const playerBScore = exits.filter(tile => tile.piece?.owner === board.beta).length;
        return playerAScore === exits.length || playerBScore === exits.length;
    }

    static highlightTiles(board: Board, coords: Coords[], color: string): Board {
        let tiles = coords.map(c => this.getTile(board, c));

        const updatedTiles = board.tiles.map(tile => {
            if (tiles.includes(tile)) {
                tile.highlight = color;
            }
            return tile;
        });
        return new Board(board.alpha, board.beta, board.numRows, board.numCols, board.numPieces, board.piecesLeft, updatedTiles);
    }

    static highlightAllies(board: Board, player: number, color: string): Board {
        let b = this.clearHighlights(board);

        const updatedTiles = b.tiles.map(tile => {
            if (tile.piece?.owner === player && tile.type !== Tile.Type.Exit) {
                tile.highlight = color;
            }
            return tile;
        });

        return new Board(b.alpha, b.beta, b.numRows, b.numCols, b.numPieces, b.piecesLeft, updatedTiles);
    }

    static highlightEnemies(board: Board, player: number, color: string): Board {
        let b = this.clearHighlights(board);

        const updatedTiles = b.tiles.map(tile => {
            if (tile.piece != null && tile.piece.owner !== player && tile.type !== Tile.Type.Exit) {
                tile.highlight = color;
            }
            return tile;
        });

        return new Board(b.alpha, b.beta, b.numRows, b.numCols, b.numPieces, b.piecesLeft, updatedTiles);
    }

    static highlightPossibleMoves(board: Board, player: number, possibleMoves: Move[]): Board {
        let b = this.clearHighlights(board);

        possibleMoves.forEach(move => {
            if (move.piece === null) {
                b.highlightTile(move.to, "yellow");
            }
        });

        b.tiles.forEach(tile => {
            if (tile.piece?.owner === player && tile.type !== Tile.Type.Exit) {
                tile.highlight = "green";
            }
        });

        return b;
    }

    static highlightPossibleMovesForPiece(board: Board, player: number, piece: Piece, possibleMoves: Move[]): Board {
        let b = this.clearHighlights(board);

        possibleMoves.forEach(move => {
            if (move.piece === piece) {
                b.highlightTile(move.to, "yellow");
            }
        });

        b.highlightTile(piece.position, "green");

        return b;
    }

    static clearHighlights(board: Board): Board {
        const updatedTiles = board.tiles.map(tile => {
            tile.highlight = null;
            return tile;
        });
        return new Board(board.alpha, board.beta, board.numRows, board.numCols, board.numPieces, board.piecesLeft, updatedTiles);
    }

    static hasPiecesToDuel(board: Board): boolean {
        const playerAPieces = board.tiles.filter(tile => tile.piece?.owner === board.alpha && tile.type !== Tile.Type.Exit);
        const playerBPieces = board.tiles.filter(tile => tile.piece?.owner === board.beta && tile.type !== Tile.Type.Exit);

        return playerAPieces.length > 0 && playerBPieces.length > 0;
    }

    static deployPiece(board: Board, playerId: number, to: Coords): Board {
        const newPiece = new Piece(playerId, to);
        const tile = this.getTile(board, to);
        const newTile = new Tile(to, tile.type, newPiece);

        return this.updateTile(board, newTile);
    }

    static movePiece(board: Board, piece: Piece, to: Coords): Board {
        let fromTile = this.getTile(board, piece.position);
        let toTile = this.getTile(board, to);

        let newBoard = this.updateTile(board, new Tile(fromTile.coords, fromTile.type, null));
        return this.updateTile(newBoard, new Tile(to, toTile.type, piece));
    }

    static duelPiece(board: Board, duel: Duel): Board {
        const ally = duel.ally;
        const enemy = duel.enemy;
        const allyTile = this.getTile(board, ally.position);
        const enemyTile = this.getTile(board, enemy.position);

        const allyRoll = duel.duelDices[0];
        const enemyRoll = duel.duelDices[1];

        const allyWins = allyRoll > enemyRoll;

        let newBoard = this.updateTile(board, new Tile(allyTile.coords, allyTile.type, null));
        newBoard = this.updateTile(newBoard, new Tile(enemyTile.coords, enemyTile.type, null));

        if (allyWins) {
            newBoard = this.updateTile(newBoard, new Tile(allyTile.coords, allyTile.type, ally));
        } else {
            newBoard = this.updateTile(newBoard, new Tile(enemyTile.coords, enemyTile.type, enemy));
        }

        return newBoard;
    }    
}
