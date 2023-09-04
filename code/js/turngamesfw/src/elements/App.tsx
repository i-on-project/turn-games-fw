import * as React from 'react'

import {
    createBrowserRouter, 
    RouterProvider
} from 'react-router-dom'

import { Home, loadHome} from './home/Home';
import { GameInfo, loadGameInfo} from './game/GameInfo'
import { loadMatchLayout, MatchLayout} from './game/MatchLayout'
import { Me, UserInfo, loadMe, loadUserInfo } from './user/UserInfo'
import { Logout } from './user/Logout';
import { NavBar } from './NavBar';
import { Login } from './user/Login';
import { Register } from './user/Register';
import { FindMatch} from './game/FindMatch';
import { RequireAuth } from '../utils/RequireAuth';
import { GamesProvider } from '../utils/GamesContext';

import { TicTacToeBoard } from '../games/tictactoe/TicTacToeElement';
import { BattleshipBoard } from '../games/battleship/BattleshipElement';
import { CastleRunElement } from '../games/castlerun/CastleRunElement';

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
                path: "/game/:gameName/findMatch",
                element:<RequireAuth><FindMatch/></RequireAuth>
            },
        
            {
                path: "/game/:gameName/match/:matchId",
                element:<RequireAuth><MatchLayout/></RequireAuth>,
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
            ['TicTacToe', TicTacToeBoard],
            ['Battleship', BattleshipBoard],
            ['CastleRun', CastleRunElement]
        ])}>
            <RouterProvider router={router}/>
        </GamesProvider>
    )
}