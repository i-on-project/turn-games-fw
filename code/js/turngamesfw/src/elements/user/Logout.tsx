import * as React from "react"
import { useEffect, useState } from "react"
import { Navigate, useLocation } from 'react-router-dom'

import { fetchAPI } from "../../utils/fetchApi";
import { useSetLogin } from "../../utils/LoggedInContext";

export function Logout() {
    const setLogin = useSetLogin()

    const [isDone, setDone] = useState(false)

    const location = useLocation()

    useEffect(() => {
        async function logout() {
            const resp = await fetchAPI("/api/user/logout", "POST")
            setLogin(false, undefined)
            setDone(true)
        }

        logout()
    }, [])

    if (isDone) {
        return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
    }

    return <div>...</div>
}