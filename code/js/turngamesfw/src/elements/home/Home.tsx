import * as React from 'react'
import { useState, useEffect, } from 'react'
import { Navigate } from "react-router-dom";
import { GameList, MockGameList } from './GameList';

export function Home(games: Game[]) {
    return (
        <GameList {...games}/>
    )
};

export function MockHome() { 
    return MockGameList()
}
