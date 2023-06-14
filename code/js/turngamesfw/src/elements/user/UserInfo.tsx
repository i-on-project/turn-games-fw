import * as React from 'react'
import { Navigate, useLoaderData } from "react-router-dom";

import { styled } from '@mui/material/styles';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

import { fetchAPI } from '../../utils/fetchApi';

export async function loadUserInfo({params}) {
    const resp = await fetchAPI("/api/user/" + params.userId, "GET")

    return resp.body["properties"]
}

export async function loadMe({params}) {
    const resp = await fetchAPI("/api/user/me", "GET")

    return resp.body["properties"]
}

export function Me() {
    const user = useLoaderData() as User

    return (
        <Navigate to={"/user/" + user.id}></Navigate>
    )
}

export function UserInfo() {
    const user = useLoaderData() as User

    return ( 
        <Container>
            <Typography variant="h4">UserInfo</Typography>
            <Box sx={{mt: "35px", display: "flex"}}>
                <StyledBox>
                    <Typography variant="h6">Id</Typography>
                    <Typography variant="body1">{user.id}</Typography>
                </StyledBox>
                <StyledBox>
                    <Typography variant="h6">Username</Typography>
                    <Typography variant="body1">{user.username}</Typography>
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
