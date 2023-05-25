import * as React from 'react'
import { useCookies } from 'react-cookie'
import { useState } from 'react'

import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'

var navItems = [
    {name: 'Login', url: "/login"}, 
    {name: 'Register', url: "/register"}
]

export function NavBar() {
    const [cookies, setCookie, removeCookie] = useCookies(["isLogged"])

    if (cookies.isLogged == "true") {
        navItems = [
            {name: 'Me', url: "/me"},
            {name: 'Logout', url: "/logout"}
        ]
    } else {
        navItems = [
            {name: 'Login', url: "/login"}, 
            {name: 'Register', url: "/register"}
        ]
    }

    return (
        <Box sx={{ display: 'relative'}}> 
            <AppBar position="relative">
                <Toolbar>
                    <Typography variant="h4" noWrap component="div"
                    onClick={()=>{window.location.href = "/"}}
                    sx={{ flexGrow: 1, display: 'block', cursor: 'pointer' }}>
                        TurnGamesFw
                    </Typography>

                    <Box sx={{display: 'flex' }}>
                        {navItems.map((item) => ( 
                        <Button key={item.name} sx={{ color: 'white' }}
                        onClick={()=>{window.location.href = item.url}}>
                            {item.name}
                        </Button>
                        ))}
                    </Box>
                </Toolbar>
            </AppBar>
        </Box>
    );
}
