import * as React from 'react'

import {
    createBrowserRouter, 
    RouterProvider
} from 'react-router-dom'

import { Home, loadHome} from './home/Home';
import { GameInfo, loadGameInfo} from './game/GameInfo'
import { Leaderboard, MockLeaderboard } from './game/Leaderboard'
import { loadMatchLayout, MatchLayout} from './game/MatchLayout'
import { Me, UserInfo, loadMe, loadUserInfo } from './user/UserInfo'
import { Logout } from './user/Logout';
import { NavBar } from './NavBar';
import { Login } from './user/Login';
import { Register } from './user/Register';
import { FindMatch} from './game/FindMatch';
import { RequireAuthn } from '../utils/RequireAuthn';
import { GamesProvider } from '../utils/GamesContext';
import { TicTacToeBoard } from './tictactoe/TicTacToeElement';

const router = createBrowserRouter([
    {
        path: "/",
        element: <NavBar/>,
        children: [
            {
                path: "/",
                element:<Home/>,
                loader: loadHome
            },
        
            {
                path: "/game/:gameName",
                element:<GameInfo/>,
                loader: loadGameInfo
            },
        
            {
                path: "/game/:gameName/leaderboard",
                element:<MockLeaderboard/>
            },

            {
                path: "/game/:gameName/findMatch",
                element:<RequireAuthn><FindMatch/></RequireAuthn>
            },
        
            {
                path: "/game/:gameName/match/:matchId",
                element:<RequireAuthn><MatchLayout/></RequireAuthn>,
                loader: loadMatchLayout
            },
        
            {
                path: "/me",
                element:<Me/>,
                loader: loadMe
            },

            {
                path: "/user/:userId",
                element:<UserInfo/>,
                loader: loadUserInfo
            },
        
            {
                path: "/login",
                element:<Login/>
            },
        
            {
                path: "/register",
                element:<Register/>
            },
        
            {
                path: "/logout",
                element:<Logout/>
            },
        ]
    },
])

export function App() {
    return (
        <GamesProvider gamesComponents={new Map([
            ['TicTacToe', TicTacToeBoard]
        ])}>
            <RouterProvider router={router}/>
        </GamesProvider>
    )
}