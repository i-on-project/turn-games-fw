import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";

export function MatchLayout(match: Match) {
    var players: User[] = [
        new User(0, "User A"),
        new User(1, "User B"),
    ];

    return (
        <>
            <MatchStatus gameName = {match.gameName} currTurn={match.currTurn} currPlayer={match.currPlayer} players={players}/>
            <br/>
            <MatchContent info = {match.info}/>
        </>
    )
};

function MatchStatus(state: {gameName: string, currTurn: number, currPlayer: number, players: User[]}) {
    return (
        <>
            <h1 className='MatchStateGameName'>{state.gameName}</h1>
            <h2 className='MatchStateCurrTurn'>Turn: {state.currTurn}</h2>
            {state.players.forEach( p => {
                <h2 className='MatchStatePlayer'>Player: {p.name}</h2>
            })}
        </>
    )
}

function MatchContent(match: {info: string}) { 
    return (
        <>
            <h2 className='MatchContentInfo'>{match.info}</h2>
        </>
    )
}

export function MockMatchLayout() { return MatchLayout(exapleMatch)}

const exapleMatch: Match = {id: "1", gameName: 'TicTacToe', state: MatchState.ON_GOING, currTurn: 1, currPlayer: 1, players: [0, 1], deadlineTurn: new Date(), created: new Date(), info: 'Player 1\'s turn'}
