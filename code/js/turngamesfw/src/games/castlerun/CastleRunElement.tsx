import * as React from 'react';
import { useEffect, useReducer, useState } from 'react';
import { Box, Grid, Divider, Typography } from '@mui/material';
import { PiecesLeftElement } from './elements/PiecesLeftElement';
import { BoardElement } from './elements/BoardElement';
import { DicesElement } from './elements/DicesElement';
import { CastleRunMatch, Move, Duel, Turn } from './CastleRunMatch';
import { Dices } from './domain/Dices';
import { Tile } from './domain/Tile';
import { Piece } from './domain/Piece';
import { Coords, equals } from './domain/Coords';
import { Board } from './domain/Board';
import { BoardLogic } from './logic/boardLogic';
import { findPossibleMoves } from './logic/boardNavegation';

enum State {
    //OPPONENTS_TURN,
    OPPONENTS_TURN,
    SEND_INFO_TO_SERVER,

    //YOUR_TURN,
    ROLL_PLAY_DICES,
    //MOVE,
    SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE,
    SELECT_TILE_TO_MOVE_THE_PIECE_TO,
    //DUEL,
    SELECT_ALLY_TO_DUEL,
    SELECT_ENEMY_TO_DUEL,
    ROLL_DUEL_DICES,
}

export function CastleRunElement(props: { match: CastleRunMatch, playerId: number, doTurn: (action: any) => void }) {
    const playerId = props.playerId;
    
    const [state, setState] = useState(props.match.currPlayer == props.playerId? State.ROLL_PLAY_DICES : State.OPPONENTS_TURN);
	const [message, setMessage] = useState(state === State.ROLL_PLAY_DICES? "Roll the dices!" : "Wait for your turn!");

	const [board, setBoard] = useState<Board>(
        new Board(
            props.match.info.alpha,
            props.match.info.beta,
            props.match.info.numRows,
            props.match.info.numCols,
            props.match.info.numPieces,
            props.match.info.piecesLeft,
            props.match.info.tiles,
    ));

    const [possibleMoves, setPossibleMoves] = useState<Move[]>([]);
    const [lastClickedTile, setLastClickedTile] = useState<Tile | null>(null);

    const [dices, setDices] = useState(new Dices());
    const [deploy, setDeploy] = useState<Move | null>(null);
    const [move, setMove] = useState<Move | null>(null);
    const [allyToDuel, setAllyToDuel] = useState<Piece | null>(null);
    const [enemyToDuel, setEnemyToDuel] = useState<Piece | null>(null);
    
    useEffect(() => { 
        setBoard( new Board(
            props.match.info.alpha,
            props.match.info.beta,
            props.match.info.numRows,
            props.match.info.numCols,
            props.match.info.numPieces,
            props.match.info.piecesLeft,
            props.match.info.tiles,
        ));

        setState(props.match.currPlayer == props.playerId? State.ROLL_PLAY_DICES : State.OPPONENTS_TURN);
    }, [props.match]);

    useEffect(() => {
        switch (state) {
            case State.ROLL_PLAY_DICES:
                setMessage("Roll the play dices!");
                setBoard(BoardLogic.clearHighlights(board));
                setDices(new Dices());
                break;

            case State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE:
                setMessage("Select a piece to move or a deploy tile!");

                let newPossibleMoves = findPossibleMoves(board, playerId, dices.play.sum());

                setPossibleMoves(newPossibleMoves);
                setBoard(BoardLogic.highlightPossibleMoves(board, playerId, newPossibleMoves));
                break;
            case State.SELECT_TILE_TO_MOVE_THE_PIECE_TO:
                setMessage("Select a tile to move the piece to!");
                
                setBoard(BoardLogic.highlightPossibleMovesForPiece(board, playerId, lastClickedTile!.piece!, possibleMoves));
                break;
            case State.SELECT_ALLY_TO_DUEL:
                setMessage("Select an ally to duel!");

                setBoard(BoardLogic.highlightAllies(board, playerId, 'green'));
                break;
            case State.SELECT_ENEMY_TO_DUEL:
                setMessage("Select an enemy to duel!");

                setBoard(BoardLogic.highlightEnemies(board, playerId, 'red'));
                break;
            case State.ROLL_DUEL_DICES:
                setMessage("Roll the duel dices!");

                setBoard(BoardLogic.clearHighlights(board));
                dices.duel.canRoll = true;
                break;
            case State.OPPONENTS_TURN:
                setMessage("Wait for your turn!");

                setBoard(BoardLogic.clearHighlights(board));
                setDices(new Dices());
                dices.play.canRoll = false;
                
                setPossibleMoves([]);
                setLastClickedTile(null);
                
                setAllyToDuel(null);
                setEnemyToDuel(null);
                setDeploy(null);
                setMove(null);
                break;
            case State.SEND_INFO_TO_SERVER:
                setMessage("Sending info to server...");
                sendInfoToServer();
                break;
            default:
                break;
        }
    }, [state]);

    ///////////////////////////////////////// ROLL DICES /////////////////////////////////////////

    function rollPlayDices() {
        if (state !== State.ROLL_PLAY_DICES) return;
    
        dices.play.roll();
    
        if (dices.play.areEqual() && BoardLogic.hasPiecesToDuel(board)) {
            setState(State.SELECT_ALLY_TO_DUEL);
        } else {
            setState(State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE);
        }
    }

    function rollDuelDices() {
        if (state !== State.ROLL_DUEL_DICES) return;
        
        dices.duel.roll();

        setState(State.SEND_INFO_TO_SERVER);
    }

    ///////////////////////////////////////// SELECT TILE /////////////////////////////////////////

    function selectTile(tile: Tile) {
        // Check if it makes sence to click the tile
        if (
            state === State.OPPONENTS_TURN
            || state === State.ROLL_PLAY_DICES
            || state === State.ROLL_DUEL_DICES
        ) return;

        if (state === State.SELECT_ALLY_TO_DUEL) {
            if (tile.piece === null || tile.piece.owner !== playerId) return;
            
            console.log("Ally to duel: " + tile.piece?.owner);

            setAllyToDuel(tile.piece);
            setState(State.SELECT_ENEMY_TO_DUEL);
            return;
        }

        if (state === State.SELECT_ENEMY_TO_DUEL) {
            if (tile.piece === null || tile.piece.owner === playerId) return;
            
            console.log("Enemy to duel: " + tile.piece?.owner);

            setEnemyToDuel(tile.piece);
            setState(State.ROLL_DUEL_DICES);
            return;
        }

        if (state === State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE) {
            if (tile.piece !== null && tile.piece.owner === playerId) {
                setLastClickedTile(tile);
                setState(State.SELECT_TILE_TO_MOVE_THE_PIECE_TO);
                return;
            }

            if (tile.piece === null) {
                let deploy = possibleMoves.find(move => equals(move.to, tile.coords));
                if (deploy === undefined) return;
                setDeploy(deploy);
                setState(State.SEND_INFO_TO_SERVER);
                return;
            }
        }

        if (state === State.SELECT_TILE_TO_MOVE_THE_PIECE_TO) {
            if (tile.piece !== null && tile.piece.owner === playerId) {
                setLastClickedTile(null);
                setState(State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE);
                return;
            }

            let move = possibleMoves.find(move => equals(move.to, tile.coords) && move.piece === lastClickedTile!.piece);
            if (move === undefined) return;
            setMove(move);
            setState(State.SEND_INFO_TO_SERVER);
            return;
        }

        if (lastClickedTile === null) {
            setLastClickedTile(tile);
        }
    }

    ///////////////////////////////////////// Send Info To Server /////////////////////////////////////////

    function sendInfoToServer() {   
        if (allyToDuel != null && enemyToDuel != null) {
            let duel = { 
                ally: allyToDuel!, 
                enemy: enemyToDuel!, 
                duelDices: {
                    dice1: dices.duel.dice1,
                    dice2: dices.duel.dice2,
                },
                duelNumber: dices.duel.dice1,
            };
            props.doTurn({type: "duel", move: null, duel: duel});
        }

        else if (deploy != null) {
            props.doTurn({type: "move", move: deploy, duel: null});
        }

        else if (move != null) {
            props.doTurn({type: "move", move: move, duel: null});
        }

        setState(State.OPPONENTS_TURN);
    }

    return (
        <Box>
            <Box display="flex" justifyContent="center" alignItems="center" marginBottom={"5px"}>
                <Typography variant="h4" gutterBottom component="div" textAlign={"center"}>
                    {message}
                </Typography>
            </Box>
            <Grid container spacing={3}>
                <PiecesLeftElement player={board.alpha} piecesLeft={board.piecesLeft.forAlpha} color={'red'} />
                <BoardElement board={board} onSelectTile={selectTile}/>
                <PiecesLeftElement player={board.beta} piecesLeft={board.piecesLeft.forBeta} color={'blue'} />
            </Grid>
            <Grid container spacing={2}>
                <DicesElement dices={dices} onPlayRoll={rollPlayDices} onDuelRoll={rollDuelDices}/>
            </Grid>
        </Box>
    );
}
