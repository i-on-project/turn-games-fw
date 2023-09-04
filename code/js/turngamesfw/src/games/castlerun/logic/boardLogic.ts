import { Board } from "../domain/Board";
import { Coords } from "../domain/Coords";
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
                console.log(tile.piece);
                tile.highlight = color;
            }
            return tile;
        });

        return new Board(b.alpha, b.beta, b.numRows, b.numCols, b.numPieces, b.piecesLeft, updatedTiles);
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
}
