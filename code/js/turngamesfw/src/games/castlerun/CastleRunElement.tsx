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
    OPPONENTS_TURN,
    ROLL_PLAY_DICES,
    //MOVE,
    SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE,
    SELECT_TILE_TO_MOVE_THE_PIECE_TO,
    //DUEL,
    SELECT_ALLY_TO_DUEL,
    SELECT_ENEMY_TO_DUEL,
    ROLL_DUEL_DICES,
    //SEND INFO TO SERVER
    SEND_INFO_TO_SERVER,
}

export function CastleRunElement(props: { match: CastleRunMatch, playerId: number, onMatchUpdate: (match: Match) => void, doAction: (action: any) => void }) {
    const [match, dispatchMatch] = useState(props.match);
    const playerId = props.playerId;
    
    const [state, setState] = useState(match.currPlayer == props.playerId? State.ROLL_PLAY_DICES : State.OPPONENTS_TURN);
	const [message, setMessage] = useState(state === State.ROLL_PLAY_DICES? "Roll the dices!" : "Wait for your turn!");

	const [dices, setDices] = useState(new Dices());
    const [board, setBoard] = useState<Board>(
        new Board(
            match.info.alpha,
            match.info.beta,
            match.info.numRows,
            match.info.numCols,
            match.info.numPieces,
            match.info.piecesLeft,
            match.info.tiles,
    ));

    const [possibleMoves, setPossibleMoves] = useState<Move[]>([]);
    const [lastClickedTile, setLastClickedTile] = useState<Tile | null>(null);
    const [deploy, setDeploy] = useState<Move | null>(null);
    const [move, setMove] = useState<Move | null>(null);
    const [allyToDuel, setAllyToDuel] = useState<Piece | null>(null);
    const [enemyToDuel, setEnemyToDuel] = useState<Piece | null>(null);
    
    useEffect(() => { props.onMatchUpdate(match);}, [match]);
    useEffect(() => { setBoard(match.info); }, [match.info]);

    useEffect(() => {
        switch (state) {
            case State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE:
                highlightAllies();
                highlightPossibleDeploys();
                break;
            case State.SELECT_TILE_TO_MOVE_THE_PIECE_TO:
                setBoard(BoardLogic.clearHighlights(board))
                highlightTilesPieceCanMoveTo();
                break;
            case State.SELECT_ALLY_TO_DUEL:
                highlightAllies();
                break;
            case State.SELECT_ENEMY_TO_DUEL:
                highlightEnemies();
                break;
            case State.OPPONENTS_TURN:
                setDices(new Dices());
                setPossibleMoves([]);
                setLastClickedTile(null);
                setAllyToDuel(null);
                break;
            case State.SEND_INFO_TO_SERVER:
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
                    
                    props.doAction({type: "duel", move: null, duel: duel});
                    return;
                }

                if(deploy != null) {
                    props.doAction({type: "move", move: deploy, duel: null});
                    return;
                }

                if(move != null) {
                    props.doAction({type: "move", move: move, duel: null});
                    return;
                }
                break;
            default:
                break;
        }
    }, [state]);

    ///////////////////////////////////////// ROLL DICES /////////////////////////////////////////

    function rollPlayDices() {
        if (state !== State.ROLL_PLAY_DICES) return;
    
        dices.play.roll();
        setDices(dices);
    
        if (dices.play.areEqual() && BoardLogic.hasPiecesToDuel(board)) {
            dices.duel.canRoll = true;
            setState(State.SELECT_ALLY_TO_DUEL);
            setMessage("Select an ally to duel!");
        } else {
            setState(State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE);
            setMessage("Select a piece to move or a deploy tile!");
            setPossibleMoves(findPossibleMoves(board, playerId, dices.play.sum()));
        }
    }

    function rollDuelDices() {
        if (state !== State.ROLL_DUEL_DICES) return;
        
        dices.duel.roll();
        setDices(dices);

        setState(State.SEND_INFO_TO_SERVER);
    }

    ///////////////////////////////////////// HIGHLIGHTS /////////////////////////////////////////

    function highlightPossibleDeploys() {
        if (state !== State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE) return;

        let deploys = possibleMoves.filter(move => move.piece === null);
        let coords = deploys.map(move => move.to);

        setBoard(BoardLogic.highlightTiles(board, coords, 'yellow'));
    }

    function highlightTilesPieceCanMoveTo() {
        if (state !== State.SELECT_TILE_TO_MOVE_THE_PIECE_TO) return;

        let piece = lastClickedTile!.piece!;        
        let coords = possibleMoves.filter(move => move.piece === piece).map(move => move.to);

        setBoard(BoardLogic.highlightTiles(board, coords, 'yellow'));
    }

    function highlightAllies() {
        if (state !== State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE && state !== State.SELECT_ALLY_TO_DUEL) return;
        
        setBoard(BoardLogic.highlightAllies(board, playerId, 'green'));
    }

    function highlightEnemies() {
        if (state !== State.SELECT_ENEMY_TO_DUEL) return;
        
        setBoard(BoardLogic.highlightEnemies(board, playerId, 'red'));
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
            setAllyToDuel(tile.piece);
            setState(State.SELECT_ENEMY_TO_DUEL);
            setMessage("Select an enemy to duel!");
            return;
        }

        if (state === State.SELECT_ENEMY_TO_DUEL) {
            if (tile.piece === null || tile.piece.owner === playerId) return;
            setEnemyToDuel(tile.piece);
            setState(State.ROLL_DUEL_DICES);
            setMessage("Roll the dices!");
            return;
        }

        if (state === State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE) {
            if (tile.piece !== null && tile.piece.owner === playerId) {
                setLastClickedTile(tile);
                setState(State.SELECT_TILE_TO_MOVE_THE_PIECE_TO);
                setMessage("Select a tile to move the piece to!");
                return;
            }

            if (tile.piece === null) {
                let deploy = possibleMoves.find(move => equals(move.to, tile.coords));
                if (deploy === undefined) return;
                setDeploy(deploy);
                setState(State.SEND_INFO_TO_SERVER);
                setMessage("Sending info to server!");
                return;
            }
        }

        if (state === State.SELECT_TILE_TO_MOVE_THE_PIECE_TO) {
            if (tile.piece !== null && tile.piece.owner === playerId) {
                setLastClickedTile(null);
                setState(State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE);
                setMessage("Select a piece to move or a deploy tile!");
                return;
            }

            let move = possibleMoves.find(move => equals(move.to, tile.coords) && move.piece === lastClickedTile!.piece);
            if (move === undefined) return;
            setMove(move);
            setState(State.SEND_INFO_TO_SERVER);
            setMessage("Sending info to server!");
            return;
        }

        if (lastClickedTile === null) {
            setLastClickedTile(tile);
        }
    }

    return (
        <Box>
            <Box display="flex" justifyContent="center" alignItems="center" marginBottom={"5px"}>
                <Typography variant="h4" gutterBottom component="div" textAlign={"center"}>
                    {message}
                </Typography>
            </Box>
            <Grid container spacing={3}>
                <PiecesLeftElement player={match.info.alpha} piecesLeft={match.info.piecesLeft.forAlpha} color={'red'} />
                <BoardElement board={match.info} onSelectTile={selectTile}/>
                <PiecesLeftElement player={match.info.alpha} piecesLeft={match.info.piecesLeft.forAlpha} color={'blue'} />
            </Grid>
            <Grid container spacing={2}>
                <DicesElement dices={dices} onPlayRoll={rollPlayDices} onDuelRoll={rollDuelDices}/>
            </Grid>
        </Box>
    );
}
