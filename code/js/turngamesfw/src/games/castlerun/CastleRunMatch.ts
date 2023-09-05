import { Board } from "./domain/Board";
import { Coords } from "./domain/Coords";
import { Piece } from "./domain/Piece";
import { BoardLogic } from "./logic/boardLogic";

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
    applyMove: (board: Board, move: Move) => {
        if (move.piece === null) {
            throw new Error("Piece cannot be null");
        }

        BoardLogic.movePiece(board, move.piece, move.to);
    },

    applyDeploy: (board: Board, move: Move, player: number) => {
        if (move.piece === null) {
            throw new Error("Piece cannot be null");
        }

        BoardLogic.deployPiece(board, player, move.to);
    },

    applyDuel: (board: Board, duel: Duel) => {
        BoardLogic.duelPiece(board, duel);
    },

    rollDice: () => Math.floor(Math.random() * 6) + 1,
    rollDices: () => [CastleRunLogic.rollDice(), CastleRunLogic.rollDice()],
}