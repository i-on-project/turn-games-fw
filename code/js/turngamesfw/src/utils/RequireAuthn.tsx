import * as React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useCookies } from "react-cookie";

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
    const [cookies, setCookie, removeCookie] = useCookies(["login"])

    const isLogged = cookies.login
    const location = useLocation()

    if (cookies.login != undefined && cookies.login.loggedin == true) {
        return <>{children}</>
    } else {
        {alert("Require Authentication!")}
        return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
    }

}