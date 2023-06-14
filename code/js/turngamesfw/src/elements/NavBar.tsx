import * as React from 'react'
import { Outlet, useNavigate } from 'react-router-dom'
import { useCookies } from 'react-cookie'

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
    const [cookies, setCookie, removeCookie] = useCookies(["login"])

    if (cookies.login != undefined && cookies.login.loggedin == true) {
        navItems = [
            {name: cookies.login.username, url: "/me"},
            {name: 'Logout', url: "/logout"}
        ]
    } else {
        navItems = [
            {name: 'Login', url: "/login"}, 
            {name: 'Register', url: "/register"}
        ]
    }

    const navigate = useNavigate()

    return (
        <>
        <Box sx={{ display: 'relative'}}> 
            <AppBar position="relative">
                <Toolbar>
                    <Typography variant="h4" noWrap component="div"
                    onClick={()=>{navigate("/")}}
                    sx={{ flexGrow: 1, display: 'block', cursor: 'pointer' }}>
                        TurnGamesFw
                    </Typography>

                    <Box sx={{display: 'flex' }}>
                        {navItems.map((item) => ( 
                        <Button key={item.name} sx={{ color: 'white' }}
                        onClick={()=>{navigate(item.url)}}>
                            {item.name}
                        </Button>
                        ))}
                    </Box>
                </Toolbar>
            </AppBar>
        </Box>
        <div className='content'>
            <Outlet/>
        </div>
        </>
    );
}
