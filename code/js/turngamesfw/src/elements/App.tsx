import * as React from 'react'

import {
    createBrowserRouter, 
    RouterProvider
} from 'react-router-dom'

import { Home, loadHome, MockHome } from './home/Home';
import { GameInfo, loadGameInfo, MockGameInfo } from './game/GameInfo'
import { Leaderboard, MockLeaderboard } from './game/Leaderboard'
import { MatchLayout, MockMatchLayout } from './game/MatchLayout'
import { UserInfo, MockUserInfo } from './user/UserInfo'
import { Login, Register } from './user/LoginAndRegister'
import { Logout } from './user/Logout';
import { NavBar } from './NavBar';

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
                path: "/game/:gameName/match/:matchId",
                element:<MockMatchLayout/>
            },
        
            {
                path: "/user/:userId",
                element:<MockUserInfo/>
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
        <>
            <RouterProvider router={router}/>
        </>
    )
}