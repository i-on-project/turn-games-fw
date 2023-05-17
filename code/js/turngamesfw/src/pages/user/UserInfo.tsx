import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";

export function UserInfo(user: User) {
    return ( <>
        <h1>{user.name}</h1>
    </>)
}

export function MockUserInfo() { return UserInfo(exampleUser) }

const exampleUser: User = {name: 'User1', id: 1}