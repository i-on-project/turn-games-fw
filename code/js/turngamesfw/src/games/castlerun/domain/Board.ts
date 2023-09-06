import { PiecesLeft } from "./PiecesLeft";
import { Tile } from "./Tile";
import { Coords, equals } from "./Coords";

export class Board {
    alpha: number;
    beta: number;
    numRows: number;
    numCols: number;
    numPieces: number;
    piecesLeft: PiecesLeft;
    tiles: Tile[];
    
    constructor(alpha: number, beta: number, numRows: number, numCols: number, numPieces: number, piecesLeft: PiecesLeft, tiles: Tile[]) {
        this.alpha = alpha;
        this.beta = beta;
        this.numRows = numRows;
        this.numCols = numCols;
        this.numPieces = numPieces;
        this.piecesLeft = piecesLeft;
        this.tiles = tiles;
    }

    highlightTile(tileCoords: Coords, color: string): void {
        const tile = this.tiles.find(tile => equals(tile.coords, tileCoords));
        if (tile) { tile.highlight = color; }
    }
}