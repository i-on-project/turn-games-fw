import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';


const defaultTheme = createTheme(
    {
        palette: {
            mode: 'dark',
            primary: {
                main: '#90caf9',
            },
            secondary: {
                main: '#f48fb1',
            },
        },
    },
);

export function Login() {
    return LoginAndRegisterForm({header: "Login", goToMessage: "Don't have an account? Sign Up", goToLink: "/register"});
}

export function Register() {
    return LoginAndRegisterForm({header: "Register", goToMessage: "Already have an account? Sign In", goToLink: "/login"});
}

function LoginAndRegisterForm(props: {header: string, goToMessage: string, goToLink: string}) {
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    console.log({
      username: data.get('username'),
      password: data.get('password'),
    });
  };

  return (
    <ThemeProvider theme={defaultTheme}>
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <Box
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
            <LockOutlinedIcon />
          </Avatar>

            {/* Login title */}
          <Typography component="h1" variant="h5"> {props.header} </Typography>

          <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>  
            {/* Username and password fields */}
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

            {/* Submit button */}
            <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
              Submit
            </Button>

            {/* Go to register */}
            <Grid container>
              <Grid item xs/>
              <Grid item>
                <Link href={props.goToLink} variant="body2"> {props.goToMessage} </Link>
              </Grid>
            </Grid>

          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  );
}