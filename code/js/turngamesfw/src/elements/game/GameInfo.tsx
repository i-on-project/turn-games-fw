import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useLoaderData, useNavigate } from "react-router-dom";
import { styled } from '@mui/material/styles';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';

import { fetchAPI } from '../../utils/fetchApi';

export async function loadGameInfo({params}) {
    const resp = await fetchAPI("/api/game/" + params.gameName, "GET")

    return resp.body["properties"]
}

export function GameInfo() {
    const game = useLoaderData() as Game

    const navigate = useNavigate()

    const goToLeaderboard = () => navigate("/game/" + game.name + "/leaderboard")
    const findMatch = () => { console.log("find match requested for game " + game.name) }
    
    return ( 
        <Container>
            <Box sx={{display: "flex", mt: "10px", justifyContent: "space-between", alignItems: "center"}}>
                <Box>
                    <Typography variant="h4">{game.name}</Typography> 
                    <Typography variant="subtitle2" color="textSecondary">Number of players: {game.numPlayers}</Typography>
                </Box>        
                <Box>
                    <StyledButton variant="contained" color="primary" onClick={goToLeaderboard}>Go to Leaderboard</StyledButton>
                    <StyledButton variant="contained" color="primary" onClick={findMatch}>Start a Match</StyledButton>
                </Box>
            </Box>

            <Box sx={{mt: "35px", display: "flex"}}>
                <StyledBox>
                    <Typography variant="h6">Description</Typography>
                    <Typography variant="body1">{game.description}</Typography>
                </StyledBox>
                <StyledBox>
                    <Typography variant="h6">Rules</Typography>
                    <Typography variant="body1">{game.rules}</Typography>
                </StyledBox>
            </Box>
        </Container>
    )
}

const StyledBox = styled(Box)(({ theme }) => ({
    flex: 1, 
    marginRight: '1.5rem', 
    width: 0.5, 
    wordWrap: "break-word", 
    whiteSpace: 'pre-line',
}));

const StyledButton = styled(Button)(({ theme }) => ({
    marginTop: '15px',
    marginRight: '5px',
    marginLeft: '5px',
    height: 0.5,
    padding: '1.2rem',
}));

export function MockGameInfo() { return GameInfo() }

const exampleGame: Game = {name: 'TicTacToe', numPlayers: 2, description: 'A classic game of TicTacToe', rules: 'Get 3 in a row to win'}