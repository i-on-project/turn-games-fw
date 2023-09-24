import * as React from 'react'
import { useState, useContext, useEffect } from "react"
import { useNavigate } from 'react-router-dom'
import { useCookies } from 'react-cookie'

import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import Link from '@mui/material/Link'
import Grid from '@mui/material/Grid'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Container from '@mui/material/Container'

import { fetchAPI } from '../../utils/fetchApi'
import { LoginInputModel } from '../../models/user/InputModels'

export function Login() {
	const [cookies, setCookie, removeCookie] = useCookies(["login"]);
	const [error, setError] = useState(undefined)

	const navigate = useNavigate()
	const goTo = () => navigate("/register")
	const afterAction = () => navigate("/")

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const data = new FormData(event.currentTarget);

		const username = data.get('username').toString()
		const password = data.get('password').toString()

		if (username == "") {
            setError("Username missing")
            return
        }
        if (password == "") {
            setError("Password missing")
            return
        }

		const resp = await fetchAPI("/api/user/login", "POST", new LoginInputModel(username, password), false)
		switch (resp.status) {
            case 200: {
				setError(undefined)
				setCookie("login", {
					loggedin: true,
					username: username
				}, { path: "/" })
				afterAction()
                break
            }
            case 400: {
				setError("Invalid username or password")
                break
            }
        }
	};

	return (
		<Container component="main" maxWidth="xs">
			<Typography component="h1" variant="h4" align='center'> Login </Typography>

			<Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
				<TextField
					margin="normal"
					required
					fullWidth
					id="username"
					label="Username"
					name="username"
					autoComplete="username"
					autoFocus
				/>

				<TextField
					margin="normal"
					required
					fullWidth
					name="password"
					label="Password"
					type="password"
					id="password"
					autoComplete="current-password"
				/>

				<Typography component='div' variant="caption" align='center'
				sx={{color: 'red'}}>
					{error}
				</Typography> 

				<Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
					Submit
				</Button>

				<Grid container>
					<Grid item xs />
					<Grid item>
						<Link variant="body2" onClick={goTo} sx={{cursor: "pointer"}}> Don't have an account? Sign Up </Link>
					</Grid>
				</Grid>
			</Box>
		</Container>
	);
}