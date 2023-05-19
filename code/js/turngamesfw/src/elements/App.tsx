import * as React from 'react'

import {
    createBrowserRouter, 
    RouterProvider
} from 'react-router-dom'

import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';


import { Home, MockHome } from './home/Home';
import { GameInfo, MockGameInfo } from './game/GameInfo'
import { Leaderboard, MockLeaderboard } from './game/Leaderboard'
import { MatchLayout, MockMatchLayout } from './game/Match'
import { UserInfo, MockUserInfo } from './user/UserInfo'
import { Login, Register } from './home/LoginAndRegister'
import NavBar from './NavBar'

const router = createBrowserRouter([
    {
        path: "/",
        element:<MockHome/> 
    },

    {
        path: "/nav",
        element:<NavBar/>
    },

    {
        path: "/game/:gameName",
        element:<MockGameInfo/>
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
])

export function App() {
    return (
        <div className='main'>
            <NavBar/>
            <RouterProvider router={router}/>
        </div>
    )
}