import * as React from 'react'
import { useState } from "react"
import { useNavigate } from 'react-router-dom'

import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import Link from '@mui/material/Link'
import Grid from '@mui/material/Grid'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Container from '@mui/material/Container'

import { fetchAPI } from '../../utils/fetchApi'
import { RegisterInputModel } from '../../models/user/InputModels'

export function Register() {

	const [error, setError] = useState(undefined)
    const [registered, setResgistered] = useState(false)

	const navigate = useNavigate()
	const goTo = () => navigate("/login")
	const afterAction = () => navigate("/login")

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const data = new FormData(event.currentTarget);

		const username = data.get('username').toString()
		const password = data.get('password').toString()

		const resp = await fetchAPI("/api/user/register", "POST", new RegisterInputModel(username, password), false)
		switch (resp.status) {
            case 201: {
				setError(undefined)
                setResgistered(true)
                break
            }
            case 400: {
				setError("Username taken")
                break
            }
        }
		
	};

    if (registered == true) {
        return (
            <Container component="main" maxWidth="xs">
                <Typography component="h1" variant="h4" align='center'>Register</Typography>
                <Typography variant="h6" align='center'>Registered with success</Typography>
                <Typography align='center'><Link variant="body1" onClick={afterAction} sx={{cursor: "pointer"}}>Login here</Link></Typography>
            </Container>
        )
    }

	return (
		<Container component="main" maxWidth="xs">
			<Typography component="h1" variant="h4" align='center'> Register </Typography>

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
						<Link variant="body2" onClick={goTo} sx={{cursor: "pointer"}}> Already have an account? Sign In </Link>
					</Grid>
				</Grid>
			</Box>
		</Container>
	);
}

