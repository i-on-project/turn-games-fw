import * as React from 'react'
import { createRoot } from 'react-dom/client'

import { App } from './elements/App'
import { NavBar } from './elements/NavBar'
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material'

const root = createRoot(document.getElementById("mainDiv"))

const defaultTheme = createTheme(
    {
        palette: {
            mode: 'light',
            primary: {
                main: '#252525',
              },
              secondary: {
                main: '#737373',
              },
          },
    },
);

root.render(
    <ThemeProvider theme={defaultTheme}>
        <CssBaseline />
        <NavBar/>
        <App/>
    </ThemeProvider>
)