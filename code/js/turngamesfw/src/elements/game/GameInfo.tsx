import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";

export function GameInfo(game: Game) {
    return ( <div className="GameInfo">
        <h1>{game.name}</h1>
        <div className="info">
            <div className="DescriptionAndRules">
                <h1>Description</h1>
                <p>{game.description}</p>
            </div>
            
            <div>
                <h1>Rules</h1>
                <p>{game.rules}</p>
            </div>
        </div>
    </div>)
}

export function MockGameInfo() { return GameInfo(exampleGame) }

const exampleGame: Game = {name: 'TicTacToe', numPlayers: 2, description: 'A classic game of TicTacToe', rules: 'Get 3 in a row to win'}