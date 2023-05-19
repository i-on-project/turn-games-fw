import * as React from 'react'
import { useState, useEffect, } from 'react'
import { Navigate } from "react-router-dom";

import { GameList, MockGameList } from './GameList';

export function Home(games: Game[]) {
    return (<>
            <div className="navbar">
            </div>

            <div className="home-content">
                <GameList {...games}/>
            </div>
        </>
    )
};

export function MockHome() { 
    return (
        <>
            <div className="navbar">
            </div>

            <div className="home-content">
                <MockGameList/>
            </div>
        </>
    )
 }