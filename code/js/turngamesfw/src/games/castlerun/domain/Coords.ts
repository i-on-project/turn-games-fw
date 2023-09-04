export class Coords {
    row: number;
    col: number;

    constructor(row: number, col: number) {
        this.row = row;
        this.col = col;
    }
}

export enum Direction {
    Up,
    Down,
    Left,
    Right,
}

export function getNext(coords: Coords, direction: Direction): Coords {
    switch (direction) {
        case Direction.Up:
            return new Coords(coords.row - 1, coords.col);
        case Direction.Down:
            return new Coords(coords.row + 1, coords.col);
        case Direction.Left:
            return new Coords(coords.row, coords.col - 1);
        case Direction.Right:
            return new Coords(coords.row, coords.col + 1);
    }
}

export function equals(coords1: Coords, coords2: Coords): boolean {
    return coords1.row === coords2.row && coords1.col === coords2.col;
}