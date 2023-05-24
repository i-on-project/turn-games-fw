import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";
import { styled } from '@mui/material/styles';
import Container from '@mui/material/Container';
import TableContainer from '@mui/material/TableContainer/TableContainer';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Table from '@mui/material/Table';
import TableHead from '@mui/material/TableHead';
import TableBody from '@mui/material/TableBody';
import TableRow from '@mui/material/TableRow';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TablePagination from '@mui/material/TablePagination';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import IconButton from '@mui/material/IconButton';
import KeyboardArrowLeft from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRight from '@mui/icons-material/KeyboardArrowRight';

export function Leaderboard(gameName: string, users: LeaderboardUser[]) {
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);
    const leaderboardUsers = users.map(u => <LeaderboardElement {...u}/>)

    const handlePreviousPage = () => {
        setPage(page - 1)
        //TODO: update the page number to be the first page using the fetch function
    }

    const handleNextPage = () => {
        setPage(page + 1)
        //TODO: update the page number to be the first page using the fetch function
    }

    return (
        <Container>
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mt: "10px" }}>
                <Typography variant="h4">{gameName} Leaderboard</Typography>

                <Box>
                    <IconButton onClick={handlePreviousPage} aria-label="Previous Page">
                        <KeyboardArrowLeft />
                    </IconButton>
                    
                    <IconButton onClick={handleNextPage} aria-label="Next Page">
                        <KeyboardArrowRight />
                    </IconButton>
                </Box>
            </Box>

            <Box sx={{display: "flex", mt: "20px"}}/>

            <TableContainer>
                <Table sx={{tableLayout: "fixed"}}>
                    <TableHead>
                        <TableRow>
                            <StyledTableCell sx={{width: 0.2}}>Position</StyledTableCell>
                            <StyledTableCell sx={{width: 0.6}}>User</StyledTableCell>
                            <StyledTableCell sx={{width: 0.2}}>Rating</StyledTableCell>
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {leaderboardUsers}
                    </TableBody>

                </Table>
            </TableContainer>
        </Container>
    )
}

function LeaderboardElement(user: LeaderboardUser) {
    const navigate = useNavigate()

    const goToUser = () => navigate("/user/" + user.id)

    return (
        <StyledTableRow onClick={goToUser}>
            <StyledTableCell> {user.position} </StyledTableCell>
            <StyledTableCell> {user.username} </StyledTableCell>
            <StyledTableCell> {user.rating} </StyledTableCell>
        </StyledTableRow>
    )
}

const StyledTableRow = styled(TableRow)(({ theme }) => ({
    '&:nth-of-type(odd)': { backgroundColor: theme.palette.action.hover,},
    ":hover": { backgroundColor: theme.palette.grey[300] },
}));

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: theme.palette.grey[900],
        color: theme.palette.common.white,
        fontSize: 18,
        cursor: "default"
      },
  
      [`&.${tableCellClasses.body}`]: {
        fontSize: 18,
        color: theme.palette.grey[900],
        cursor: 'pointer',
      },
  
      textAlign: 'center',
      alignContent: 'center',
      padding: 'auto',
      margin: 'auto',
}));

export function MockLeaderboard() { return Leaderboard("TicTacToe", exampleLeaderboard) }

const exampleLeaderboard: LeaderboardUser[] = [
    {id: 1, username: 'User1', rating: 1000, position: 1},
    {id: 2, username: 'User2', rating: 900, position: 2},
    {id: 3, username: 'User3', rating: 800, position: 3},
    {id: 4, username: 'User4', rating: 700, position: 4},
    {id: 5, username: 'User5', rating: 600, position: 5},
    {id: 6, username: 'User6', rating: 500, position: 6},
    {id: 7, username: 'User7', rating: 400, position: 7},
    {id: 8, username: 'User8', rating: 300, position: 8},
    {id: 9, username: 'User9', rating: 200, position: 9},
    {id: 10, username: 'User10', rating: 100, position: 10},
]