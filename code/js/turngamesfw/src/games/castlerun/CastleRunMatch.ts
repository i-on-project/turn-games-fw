import { Board } from "./domain/Board";
import { Coords } from "./domain/Coords";
import { Piece } from "./domain/Piece";

export type CastleRunMatch = {
    id: string;
    gameName: string;
    state: MatchState;
    players: number[];
    currPlayer: number;
    currTurn: number;
    deadlineTurn: Date;
    created: Date;
    info: Board;
}

export type Move = {
    piece: Piece | null;
    to: Coords;
    distance: number;
}

export type Duel = {
    ally: Piece;
    enemy: Piece;
    duelDices: number[];
    duelNumber: number;
}

export type Turn = {
    type: "move" | "duel";
    move: Move | null;
    duel: Duel | null;
}

export const CastleRunLogic = {
    rollDice: () => Math.floor(Math.random() * 6) + 1,
    rollDices: () => [CastleRunLogic.rollDice(), CastleRunLogic.rollDice()],
}