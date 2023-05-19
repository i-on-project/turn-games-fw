import * as React from 'react'

import {
    createBrowserRouter, 
    RouterProvider
} from 'react-router-dom'

import { Home, MockHome } from './pages/home/Home'
import { GameInfo, MockGameInfo } from './pages/game/GameInfo'
import { Leaderboard, MockLeaderboard } from './pages/game/Leaderboard'
import { MatchLayout, MockMatchLayout } from './pages/game/Match'
import { UserInfo, MockUserInfo } from './pages/user/UserInfo'
import { Login } from './pages/home/Login'
import { Register } from './pages/home/Register'
import { NavBar } from './pages/NavBar'

const router = createBrowserRouter([
    {
        path: "/",
        element:<MockHome/> 
    },

    {
        path: "/homeNav",
        element: <>
            <NavBar name = {null} id = {null}/>
            <MockHome/> 
        </>
    },

    {
        path: "/nav",
        element:<NavBar name = {null} id = {null}/>
    },

    {
        path: "/nav2",
        element:<NavBar name = {"test"} id = {1}/>
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
            <NavBar name = {null} id = {null}/>
            <RouterProvider router={router}/>
        </div>
    )
}