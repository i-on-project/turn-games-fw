import * as React from 'react'
import { useState, useEffect, } from 'react'
import { Navigate, useLoaderData } from "react-router-dom";
import { GameList, MockGameList } from './GameList';

import { fetchAPI } from '../../utils/fetchApi';

export async function loadHome() {
    const resp = await fetchAPI("/api/gameList", "GET")

    return resp.body["properties"]
}

export function Home() {
    const gameNames = useLoaderData() as {gameList: string[]}

    return (
        <GameList {...gameNames}/>
    )
};

export function MockHome() { 
    return MockGameList()
}
