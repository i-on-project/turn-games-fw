export class PiecesLeft {
    A: number;
    B: number;
    forA: number;
    forB: number;

    constructor(A: number, B: number, forA: number, forB: number) {
        this.A = A;
        this.B = B;
        this.forA = forA;
        this.forB = forB;
    }

    dec(player: number): PiecesLeft {
        if (player === this.A) {
            return new PiecesLeft(this.A, this.B, this.forA - 1, this.forB);
        }
        if (player === this.B) {
            return new PiecesLeft(this.A, this.B, this.forA, this.forB - 1);
        }
        throw new Error("Player not recognized!");
    }

    inc(player: number): PiecesLeft {
        if (player === this.A) {
            return new PiecesLeft(this.A, this.B, this.forA + 1, this.forB);
        }
        if (player === this.B) {
            return new PiecesLeft(this.A, this.B, this.forA, this.forB + 1);
        }
        throw new Error("Player not recognized!");
    }
}
