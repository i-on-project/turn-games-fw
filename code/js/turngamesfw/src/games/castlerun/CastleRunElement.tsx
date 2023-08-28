import * as React from 'react';
import { useEffect, useReducer, useState } from 'react';
import { Box, Grid, Divider, Typography } from '@mui/material';
import { PiecesLeftElement } from './elements/PiecesLeftElement';
import { BoardElement } from './elements/BoardElement';
import { DicesElement } from './elements/DicesElement';
import { CastleRunMatch, Move, Duel } from './domain/CastleRunMatch';
import { Dices } from './domain/Dices';
import { Tile } from './domain/Tile';
import { Piece } from './domain/Piece';
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
}

export function CastleRunElement(props: { match: CastleRunMatch, playerId: number, onMatchUpdate: (match: Match) => void, doAction: (action: any) => void }) {
    const [match, dispatchMatch] = useState(props.match);
    const playerId = props.playerId;
    
    const [state, setState] = useState(match.currPlayer == props.playerId? State.ROLL_PLAY_DICES : State.OPPONENTS_TURN);
	const [message, setMessage] = useState(state === State.ROLL_PLAY_DICES? "Roll the dices!" : "Wait for your turn!");

	const [dices, setDices] = useState(new Dices());
    const [board, setBoard] = useState(match.info);

    const [possibleMoves, setPossibleMoves] = useState<Move[]>([]);
    const [lastClickedTile, setLastClickedTile] = useState<Tile | null>(null);
    const [allyToDuel, setAllyToDuel] = useState<Piece | null>(null);
    const [enemyToDuel, setEnemyToDuel] = useState<Piece | null>(null);
    
    useEffect(() => { props.onMatchUpdate(match);}, [match]);
    useEffect(() => { setBoard(match.info); }, [match.info]);

    useEffect(() => {
        switch (state) {
            case State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE:
                highlightPossibleDeploys();
                highlightAllies();
                break;
            case State.SELECT_TILE_TO_MOVE_THE_PIECE_TO:
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
                setBoard(board.clearHighlights());
            default:
                break;
        }
    }, [state]);

    ///////////////////////////////////////// ROLL DICES /////////////////////////////////////////

    function rollPlayDices() {
        if (state !== State.ROLL_PLAY_DICES) return;
    
        dices.play.roll();
        setDices(dices);
    
        if (dices.play.areEqual() && board.hasPiecesToDuel()) {
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

        //TODO: Fix so the winner is not based on the player id
        if (dices.duel.winner() === playerId) {
            setMessage("You won the duel!");
        } else {   
            setMessage("You lost the duel!");
        }

        setState(State.OPPONENTS_TURN);
        
        sendTurnInfo({
            ally: allyToDuel!,
            enemy: enemyToDuel!,
            duelDices: [dices.duel[0], dices.duel[1]],
            duelNumber: dices.play[0],
        });
    }

    ///////////////////////////////////////// HIGHLIGHTS /////////////////////////////////////////

    function highlightPossibleDeploys() {
        if (state !== State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE) return;

        let deploys = possibleMoves.filter(move => move.piece === null);
        let coords = deploys.map(move => move.to);

        setBoard(board.highlightTiles(coords, 'yellow'));
    }

    function highlightTilesPieceCanMoveTo() {
        if (state !== State.SELECT_TILE_TO_MOVE_THE_PIECE_TO) return;

        let piece = lastClickedTile!.piece!;        
        let coords = possibleMoves.filter(move => move.piece === piece).map(move => move.to);

        setBoard(board.highlightTiles(coords, 'yellow'));
    }

    function highlightAllies() {
        if (state !== State.SELECT_TILE_TO_DEPLOY_TO_OR_PIECE_TO_MOVE && state !== State.SELECT_ALLY_TO_DUEL) return;
        
        setBoard(board.highlightAllies(playerId, 'green'));
    }

    function highlightEnemies() {
        if (state !== State.SELECT_ENEMY_TO_DUEL) return;
        
        setBoard(board.highlightEnemies(playerId, 'red'));
    }

    ///////////////////////////////////////// SELECT TILE /////////////////////////////////////////

    function selectTile(tile: Tile) {
        // Check if it makes sence to click the tile
        if (
            state === State.OPPONENTS_TURN
            || state === State.ROLL_PLAY_DICES
            || state === State.ROLL_DUEL_DICES
        ) return;

        //In case it is a duel
        if (state === State.SELECT_ALLY_TO_DUEL) {
            if (tile.piece === null || tile.piece.owner !== playerId) return;
            setAllyToDuel(tile.piece!);
            setState(State.SELECT_ENEMY_TO_DUEL);
            setMessage("Select an enemy to duel!");
            return;
        } else if (state === State.SELECT_ENEMY_TO_DUEL) {
            if (tile.piece === null || tile.piece.owner === playerId) return;
            setEnemyToDuel(tile.piece!);
            setState(State.ROLL_DUEL_DICES);
            setMessage("Roll the duel dices!");
            return;
        }

        //In case it is a deploy or a move
        else {
            // Check if the tile is a possible deploy
            let deploy = possibleMoves.find(move => move.piece === null && move.to.equals(tile.coords));
            if (deploy) {
                sendTurnInfo(deploy);
                return;
            }

            // Check if the tile is a possible move
            let move = possibleMoves.find(move => move.piece === lastClickedTile!.piece && move.to.equals(tile.coords));
            if (move) {
                sendTurnInfo(move);
                return;
            }
        }
    }

    ///////////////////////////////////////// SEND TURN INFO /////////////////////////////////////////

    function sendTurnInfo(turn: Move | Duel) {
        let action = {
            type: 'sendTurnInfo',
            matchId: match.id,
            playerId: playerId,
            turn: turn,
        };

        setState(State.OPPONENTS_TURN);
        setMessage("Wait for your turn!");

        props.doAction(action);
    }

    return (
        <Box>
            <Box display="flex" justifyContent="center" alignItems="center" marginBottom={"5px"}>
                <Typography variant="h4" gutterBottom component="div" textAlign={"center"}>
                    {message}
                </Typography>
            </Box>
            <Grid container spacing={3}>
                <PiecesLeftElement player={match.info.playerA} piecesLeft={match.info.piecesLeft.forA} color={'red'} />
                <BoardElement board={match.info} onSelectTile={selectTile}/>
                <PiecesLeftElement player={match.info.playerA} piecesLeft={match.info.piecesLeft.forA} color={'blue'} />
            </Grid>
            <Grid container spacing={2}>
                <DicesElement dices={dices} onPlayRoll={rollPlayDices} onDuelRoll={rollDuelDices}/>
            </Grid>
        </Box>
    );
}
