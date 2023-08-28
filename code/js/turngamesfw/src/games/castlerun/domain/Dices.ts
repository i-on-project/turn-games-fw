export class Dices {
    play: DicePair = new DicePair();
    duel: DicePair = new DicePair();

    constructor(play: DicePair = new DicePair(), duel: DicePair = new DicePair()) {
        this.play = play;
        this.duel = duel;
    }
}

export class DicePair {
    dice1: number;
    dice2: number;
    canRoll: boolean;

    constructor(dice1: number = null, dice2: number = null, canRoll: boolean = true) {
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.canRoll = canRoll;
    }

    roll(): void {
        if (!this.canRoll) return

        this.dice1 = this.randomDiceValue();
        this.dice2 = this.randomDiceValue();
        this.canRoll = false;
    }

    clear(): void {
        this.dice1 = null;
        this.dice2 = null;
    }

    sum(): number {
        return this.dice1 + this.dice2;
    }

    areEqual(): boolean {
        return this.dice1 === this.dice2;
    }

    smallest(): number {
        return this.dice1 < this.dice2 ? this.dice1 : this.dice2;
    }

    biggest(): number {
        return this.dice1 > this.dice2 ? this.dice1 : this.dice2;
    }

    winner(): number {
        return this.dice1 > this.dice2 ? 1 : 2;
    }

    private randomDiceValue(): number {
        return Math.floor(Math.random() * 6) + 1;
    }
}
