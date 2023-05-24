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

export function GameList(games: Game[]) {
    const gameList = games.map(g => <GameListElement name={g.name} key={g.name}/>)

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
              {gameList}
            </TableBody>
          </Table>
        </TableContainer>
      </Container>
  );
};

function GameListElement(game: {name: string}) {
    const navigate = useNavigate()

    const goToGameInfo = () => navigate("/game/" + game.name)
    const goToLeaderboard = () => navigate("/game/" + game.name + "/leaderboard")
    const findMatch = () => { console.log("find match requested for game " + game.name) }
    
    return (
        <StyledTableRow key={game.name}>
            <StyledTableCell sx={{textAlign: 'left'}} onClick={goToGameInfo}>{game.name}</StyledTableCell>
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

const exampleGameList: Game[] = [
    {name: 'TicTacToe', numPlayers: 2, description: 'A classic game of TicTacToe', rules: 'Get 3 in a row to win'},
    {name: 'ConnectFour', numPlayers: 2, description: 'A classic game of Connect4', rules: 'Get 4 in a row to win'},
    {name: 'Chess', numPlayers: 2, description: 'A classic game of Chess', rules: 'Checkmate the opponent to win'},
    {name: 'Checkers', numPlayers: 2, description: 'A classic game of Checkers', rules: 'Capture all the opponents pieces to win'},
    {name: 'Go', numPlayers: 2, description: 'A classic game of Go', rules: 'Capture more territory than your opponent to win'},
    {name: 'CastleRun', numPlayers: 2, description: 'A classic game of CastleRun', rules: 'Get to the end of the board with 3 pieces to win'},
]
