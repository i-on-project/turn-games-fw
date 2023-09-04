import { Coords } from './Coords';

export class Piece {
    owner: number;
    position: Coords;
    frozen: number;
    king: boolean;

    constructor(
        owner: number,
        position: Coords,
        frozen: number = 0,
        king: boolean = false
    ) {
        this.owner = owner;
        this.position = position;
        this.frozen = frozen;
        this.king = king;
    }
}
