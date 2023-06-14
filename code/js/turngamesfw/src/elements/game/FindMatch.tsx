import * as React from 'react'
import { Navigate, useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from 'react';

import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';

import { fetchAPI } from '../../utils/fetchApi';

export function FindMatch() {

    const { gameName } = useParams()

    const [status, setStatus] = useState("SEARCHING")
    const [gid, setGid] = useState(undefined)

    async function foundGame(intervalId: number) {
        const resp = await fetchAPI("/api/game/" + gameName + "/found", "GET")
        if (resp.body["properties"].found) {
            clearInterval(intervalId)
            setStatus("IN_GAME")
            const gid = resp.body["properties"].game.id
            setGid(gid)
        }
    }

    async function startFindMatch() {
        const resp = await fetchAPI("/api/game/" + gameName+ "/mystate", "GET")
        if (resp.body["properties"].state == "INACTIVE") {
            await fetchAPI("/api/game/" + gameName + "/find", "POST")
        }
        const interval = setInterval(() => foundGame(interval), 1000)
    }

    useEffect(() => {
        startFindMatch()
    }, [])

    if (status == "IN_GAME") {
        return (
            <Navigate to={"/game/" + gameName + "/match/" + gid}></Navigate>
        )
    }

    return (
        <Container>
             <Typography align='center' variant="h5">{status}</Typography> 
        </Container>
    )
};