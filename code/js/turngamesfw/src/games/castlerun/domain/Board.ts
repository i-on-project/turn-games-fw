import { PiecesLeft } from "./PiecesLeft";
import { Tile } from "./Tile";

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
}