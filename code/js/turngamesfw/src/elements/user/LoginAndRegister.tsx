import * as React from 'react'
import { useState } from "react"
import { useCookies } from 'react-cookie'
import { useNavigate } from 'react-router-dom'

import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import Link from '@mui/material/Link'
import Grid from '@mui/material/Grid'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Container from '@mui/material/Container'

import { fetchAPI } from '../../utils/fetchApi'

export function Login() {
	return LoginAndRegisterForm({
		action: "/login",
		setCookie: true,
		header: "Login",
		errorMessage: "Invalid username or password",
		afterAction: "/",
		goToMessage: "Don't have an account? Sign Up",
		goToLink: "/register"
	});
}

export function Register() {
	return LoginAndRegisterForm({
		action: "/register",
		setCookie: false,
		header: "Register",
		errorMessage: "Username taken",
		afterAction: "/login",
		goToMessage: "Already have an account? Sign In",
		goToLink: "/login"
	});
}

function LoginAndRegisterForm(props: { action: string, setCookie: boolean, header: string, errorMessage: string, afterAction: string, goToMessage: string, goToLink: string }) {
	
	const [cookies, setCookie, removeCookie] = useCookies(["isLogged"]);

	const [error, setError] = useState(undefined)

	const navigate = useNavigate()
	const goTo = () => navigate(props.goToLink)
	const afterAction = () => navigate(props.afterAction)

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const data = new FormData(event.currentTarget);

		const resp = await fetchAPI("/api/user" + props.action, "POST", {
			username: data.get('username'),
			password: data.get('password'),
		}, false)
		switch (resp.status) {
            case 200: {
				setError(undefined)
				if (props.setCookie === true) {
					setCookie("isLogged", true, { path: "/" })
					afterAction()
				}
                break
            }
            case 400: {
				setError(props.errorMessage)
                break
            }
        }
		
	};

	return (
		<Container component="main" maxWidth="xs">
			<Typography component="h1" variant="h4"> {props.header} </Typography>

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
						<Link variant="body2" onClick={goTo} sx={{cursor: "pointer"}}> {props.goToMessage} </Link>
					</Grid>
				</Grid>
			</Box>
		</Container>
	);
}