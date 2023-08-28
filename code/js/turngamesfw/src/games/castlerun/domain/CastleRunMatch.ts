import { Coords } from "./Coords";
import { Board } from "./Board";
import { Piece } from "./Piece";

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

export const CastleRunLogic = {
    rollDice: () => Math.floor(Math.random() * 6) + 1,
    rollDices: () => [CastleRunLogic.rollDice(), CastleRunLogic.rollDice()],
}