import * as React from 'react'
import {
    useState,
    createContext,
    useContext,
} from 'react'

type ContextType = {
    loggedin: boolean,
    username: string,
    setLogin: (loggedin: boolean, username: string) => void
}

const LoggedInContext  = createContext<ContextType>({
    loggedin: false,
    username: undefined,
    setLogin: () => {}
})

export function LoggedInProvider({ children }: { children: React.ReactNode }) {
    const [loggedin, setLoggedin] = useState(false)
    const [username, setUsername] = useState('')

    const setLogin = (loggedin, username) => {
        setLoggedin(loggedin)
        setUsername(username)
    }

    return (
        <LoggedInContext.Provider value={{loggedin: loggedin, username: username, setLogin: setLogin}}>
            {children}
        </LoggedInContext.Provider>
    )
}

export function useLoggedIn() {
    return useContext(LoggedInContext).loggedin
}

export function useUsername() {
    return useContext(LoggedInContext).username
}

export function useSetLogin() {
    return useContext(LoggedInContext).setLogin
}
