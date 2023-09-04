import * as React from 'react'
import { useLoaderData } from "react-router-dom";
import { fetchAPI } from '../../utils/fetchApi';
import { GameList } from './GameList';

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
