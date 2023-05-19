import * as React from 'react'
import { useState, useEffect, } from 'react'
import { useNavigate } from "react-router-dom";

export function Register() {
    const [passwordType, setPasswordType] = useState("password");
    
    const togglePassword =()=>{
        if(passwordType==="password") 
            setPasswordType("text")
        else 
            setPasswordType("password")
    }

    return (
        <div className="LoginAndRegisterForm">
            <h2>Register</h2>
            <form>
                <input type="text" placeholder="Username"/>
                
                <div>
                    <span><i className={ passwordType==="password"?"fa fa-eye-slash": "fa fa-eye"} onClick={togglePassword}/></span>
                    <input type={passwordType} placeholder="Password"/>
                </div>

                <button type="submit">Register</button>
                <a className="GoToLogin" href="/login">Already have an account?</a>
            </form>
        </div>
    )
};