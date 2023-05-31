import * as React from "react"
import { useEffect, useState } from "react"
import { Navigate, useLocation } from 'react-router-dom'
import { useCookies } from "react-cookie";

import { fetchAPI } from "../../utils/fetchApi";

export function Logout() {
    const [cookies, setCookie, removeCookie] = useCookies(["login"]);

    const [isDone, setDone] = useState(false)

    const location = useLocation()

    useEffect(() => {
        async function logout() {
            const resp = await fetchAPI("/api/user/logout", "POST")
            setCookie("login", {
                loggedin: false,
                username: undefined
            }, { path: "/" })
            setDone(true)
        }

        logout()
    }, [])

    if (isDone) {
        return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
    }

    return <div>...</div>
}