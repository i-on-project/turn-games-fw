import * as React from 'react'
import { useState, useEffect, } from 'react'

export function NavBar(user: {name: string | null | undefined , id: number | undefined | null}) {

    return (
        <ul className="NavBar">
            <li>
                <a href={"/"}> Home </a></li>
            <li>
                <form>
                    <input className="navBarSearchBar" type="text" placeholder="Search for users by username!"/>
                    <i className="fa fa-search"></i>
                </form>
            </li>

            {
                (user.id === undefined || user.id === null) ? <>
                    <li><a href={"/login"}> Login </a></li> 
                    <li><a href={"/register"}> Register </a></li>
                </>
                
                : <li><a href={"/users/" + user.id}> {user.name} </a></li>}
        </ul>
    )
}
