import { Coords } from "./Coords";
import { Piece } from "./Piece";

export class Tile {
    coords: Coords;
    type: Tile.Type;
    piece: Piece | null;
    highlight: string | null = null;

    constructor(coords: Coords, type: Tile.Type = Tile.Type.Floor, piece: Piece | null = null) {
        this.coords = coords;
        this.type = type;
        this.piece = piece;
    }
}

export namespace Tile {
    export enum Type {
        Floor = "floor",
        Wall = "Wall",
        Exit = "exit",
        EntranceA = "EntranceA",
        EntranceB = "EntranceB",
    }
}
