import * as React from 'react';

import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';

const navItems = [
    {name: 'Login', url: "/login"}, 
    {name: 'Register', url: "/register"}
];

export function NavBar() {
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
