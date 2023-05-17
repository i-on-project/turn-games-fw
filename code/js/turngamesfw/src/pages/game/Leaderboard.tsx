import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";

export function Leaderboard(gameName: string, users: LeaderboardUser[]) {
    const leaderboardUsers = users.map(u => <LeaderboardElement {...u}/>)

    return (<>
        <title>{gameName} Leaderboard</title>
        <table className="leaderboard">
            <tr>
                <th className="leaderboardColName">Position</th>
                <th className="leaderboardColName">User</th>
                <th className="leaderboardColName">Rating</th>
            </tr>
            {leaderboardUsers}
        </table>
    </>)
}

function LeaderboardElement(user: LeaderboardUser) {
    const navigate = useNavigate()

    const goToUser = () => navigate("/user/" + user.id)

    return (<>
        <tr className='leaderboardElement' onClick={goToUser}>
            <th> {user.position} </th>
            <th> {user.username} </th>
            <th> {user.rating} </th>
        </tr>
    </>)
}

export function MockLeaderboard() { return Leaderboard("TicTacToe", exampleLeaderboard) }

const exampleLeaderboard: LeaderboardUser[] = [
    {id: 1, username: 'User1', rating: 1000, position: 1},
    {id: 2, username: 'User2', rating: 900, position: 2},
    {id: 3, username: 'User3', rating: 800, position: 3},
    {id: 4, username: 'User4', rating: 700, position: 4},
    {id: 5, username: 'User5', rating: 600, position: 5},
    {id: 6, username: 'User6', rating: 500, position: 6},
    {id: 7, username: 'User7', rating: 400, position: 7},
    {id: 8, username: 'User8', rating: 300, position: 8},
    {id: 9, username: 'User9', rating: 200, position: 9},
    {id: 10, username: 'User10', rating: 100, position: 10},
]