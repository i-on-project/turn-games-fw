import { Coords } from "../domain/Coords";
import { Tile } from "../domain/Tile";
import { PiecesLeft } from "../domain/PiecesLeft";

export class Board {
    playerA: number;
    playerB: number;
    numRows: number;
    numCols: number;
    numPieces: number;
    piecesLeft: PiecesLeft;
    tiles: Tile[];

    constructor(
        playerA: number,
        playerB: number,
        numRows: number = 9,
        numCols: number = 36,
        numPieces: number = 6,
        piecesLeft: PiecesLeft,
        tiles: Tile[]
    ) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.numRows = numRows;
        this.numCols = numCols;
        this.numPieces = numPieces;
        this.piecesLeft = piecesLeft;
        this.tiles = tiles;
    }

    getTile(c: Coords): Tile {
        return this.tiles[c.row * this.numCols + c.col];
    }

    private getExits(): Tile[] {
        return this.tiles.filter(tile => tile.type === Tile.Type.Exit);
    }

    isWithinBorders(c: Coords): boolean {
        return c.row >= 0 && c.row < this.numRows && c.col >= 0 && c.col < this.numCols;
    }

    updateTile(newTile: Tile): Board {
        const updatedTiles = this.tiles.map(tile => {
            if (tile.coords == newTile.coords) {
                return newTile;
            }
            return tile;
        });
        return new Board(this.playerA, this.playerB, this.numRows, this.numCols, this.numPieces, this.piecesLeft, updatedTiles);
    }
    
    isGameOver(): boolean {
        const exits = this.getExits();
        const playerAScore = exits.filter(tile => tile.piece?.owner === this.playerA).length;
        const playerBScore = exits.filter(tile => tile.piece?.owner === this.playerB).length;
        return playerAScore === exits.length || playerBScore === exits.length;
    }

    highlightTiles(coords: Coords[], color: string): Board {
        let tiles = coords.map(c => this.getTile(c));

        const updatedTiles = this.tiles.map(tile => {
            if (tiles.includes(tile)) {
                tile.highlight = color;
            }
            return tile;
        });
        return new Board(this.playerA, this.playerB, this.numRows, this.numCols, this.numPieces, this.piecesLeft, updatedTiles);
    }

    highlightAllies(player: number, color: string): Board {
        const updatedTiles = this.tiles.map(tile => {
            if (tile.piece?.owner === player && tile.type !== Tile.Type.Exit) {
                tile.highlight = color;
            }
            return tile;
        });
        return new Board(this.playerA, this.playerB, this.numRows, this.numCols, this.numPieces, this.piecesLeft, updatedTiles);
    }

    highlightEnemies(player: number, color: string): Board {
        const updatedTiles = this.tiles.map(tile => {
            if (tile.piece?.owner !== player && tile.type !== Tile.Type.Exit) {
                tile.highlight = color;
            }
            return tile;
        });
        return new Board(this.playerA, this.playerB, this.numRows, this.numCols, this.numPieces, this.piecesLeft, updatedTiles);
    }

    clearHighlights(): Board {
        const updatedTiles = this.tiles.map(tile => {
            tile.highlight = null;
            return tile;
        });
        return new Board(this.playerA, this.playerB, this.numRows, this.numCols, this.numPieces, this.piecesLeft, updatedTiles);
    }

    hasPiecesToDuel(): boolean {
        const playerAPieces = this.tiles.filter(tile => tile.piece?.owner === this.playerA && tile.type !== Tile.Type.Exit);
        const playerBPieces = this.tiles.filter(tile => tile.piece?.owner === this.playerB && tile.type !== Tile.Type.Exit);

        return playerAPieces.length > 0 && playerBPieces.length > 0;
    }
}
