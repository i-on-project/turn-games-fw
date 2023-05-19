import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";

export function GameList(games: Game[]) {
    const gameList = games.map(g => <GameListElement name={g.name}/>)

    return (
        <table className="gameList">
            <thead>
                <tr className="gameListSections">
                    <th className="gameListSectionName">Name</th>
                    <th className="gameListSectionName">LeaderBoard</th>
                    <th className="gameListSectionName">Start Match</th>
                </tr>    
            </thead>
            <tbody>
                {gameList}
            </tbody>      
        </table>
    )
};

function GameListElement(game: {name: string}) {
    const navigate = useNavigate()

    const goToGameInfo = () => navigate("/game/" + game.name)
    const goToLeaderboard = () => navigate("/game/" + game.name + "/leaderboard")
    const findMatch = () => { console.log("find match requested for game " + game.name) }
    
    return (
        <tr className='gameListElement'>
            <th onClick={goToGameInfo}>{game.name} </th>
            <th> <button className="gameListElementButton" onClick={goToLeaderboard}> <i className="fa fa-trophy"/> </button> </th>
            <th> <button className="gameListElementButton" onClick={findMatch}> <i className="fa fa-play"/> </button> </th>
        </tr>
    )
}

export function MockGameList() { return GameList(exampleGameList) }

const exampleGameList: Game[] = [
    {name: 'TicTacToe', numPlayers: 2, description: 'A classic game of TicTacToe', rules: 'Get 3 in a row to win'},
    {name: 'ConnectFour', numPlayers: 2, description: 'A classic game of Connect4', rules: 'Get 4 in a row to win'},
    {name: 'Chess', numPlayers: 2, description: 'A classic game of Chess', rules: 'Checkmate the opponent to win'},
    {name: 'Checkers', numPlayers: 2, description: 'A classic game of Checkers', rules: 'Capture all the opponents pieces to win'},
    {name: 'Go', numPlayers: 2, description: 'A classic game of Go', rules: 'Capture more territory than your opponent to win'},
    {name: 'CastleRun', numPlayers: 2, description: 'A classic game of CastleRun', rules: 'Get to the end of the board with 3 pieces to win'},
]
