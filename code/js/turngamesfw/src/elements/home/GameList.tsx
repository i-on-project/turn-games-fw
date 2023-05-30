import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";

import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Container from '@mui/material/Container';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';

export function GameList(games: {gameList: string[]}) {
    return (
      <Container>
        <TableContainer component={Paper} sx={{pt:'10px'}}>
          <Table aria-label="customized table">
            <TableHead>
              <TableRow>
                <StyledTableCell sx={{width: 0.8, textAlign: 'left'}}>Games</StyledTableCell>
                <StyledTableCell sx={{width: 0.1}}></StyledTableCell>
                <StyledTableCell sx={{width: 0.1}}></StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {games.gameList.map( gameName => (
                GameListElement(gameName)
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Container>
  );
};

function GameListElement(name: string) {
    const navigate = useNavigate()

    const goToGameInfo = () => navigate("/game/" + name)
    const goToLeaderboard = () => navigate("/game/" + name + "/leaderboard")
    const findMatch = () => { console.log("find match requested for game " + name) }
    
    return (
        <StyledTableRow key={name}>
            <StyledTableCell sx={{textAlign: 'left'}} onClick={goToGameInfo}>{name}</StyledTableCell>
            <StyledTableCell onClick={goToLeaderboard}>
                <EmojiEventsIcon sx={{ fontSize: 18 }}/>
            </StyledTableCell>
            <StyledTableCell onClick={findMatch}>
                <PlayArrowIcon sx={{ fontSize: 18 }}/>
            </StyledTableCell>
        </StyledTableRow>
    )
}

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
      backgroundColor: theme.palette.grey[900],
      color: theme.palette.common.white,
      fontSize: 18,
    },

    [`&.${tableCellClasses.body}`]: {
      fontSize: 18,
      color: theme.palette.grey[900],
      cursor: 'pointer',
    },
    [`&.${tableCellClasses.body}:hover`]: {
      fontSize: 18,
      color: theme.palette.grey[900],
      backgroundColor: theme.palette.grey[300],
      cursor: 'pointer',
    },

    textAlign: 'center',
    alignContent: 'center',
    padding: 'auto',
    margin: 'auto',
}));
  
const StyledTableRow = styled(TableRow)(({ theme }) => ({
    '&:nth-of-type(odd)': { backgroundColor: theme.palette.action.hover,},
    // hide last border
    '&:last-child td, &:last-child th': { border: 0, },
}));


export function MockGameList() { return GameList(exampleGameList) }

const exampleGameList: {gameList: string[]} = {gameList: [
    'TicTacToe',
    'ConnectFour',
    'Chess',
    'Checkers',
    'Go',
    'CastleRun',
]}
