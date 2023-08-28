export class Coords {
    row: number;
    col: number;

    constructor(row: number, col: number) {
        this.row = row;
        this.col = col;
    }

    toString(): string {
        return `{${this.row} ${this.col}}`;
    }

    getNext(direction: Direction): Coords {
        switch (direction) {
            case Direction.Up:
                return new Coords(this.row - 1, this.col);
            case Direction.Down:
                return new Coords(this.row + 1, this.col);
            case Direction.Left:
                return new Coords(this.row, this.col - 1);
            case Direction.Right:
                return new Coords(this.row, this.col + 1);
        }
    }

    equals(coords: Coords): boolean {
        return this.row === coords.row && this.col === coords.col;
    }
}

export enum Direction {
    Up,
    Down,
    Left,
    Right,
}
