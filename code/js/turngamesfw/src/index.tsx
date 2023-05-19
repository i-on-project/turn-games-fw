import * as React from 'react'
import { createRoot } from 'react-dom/client'

import { App } from './elements/App'

const root = createRoot(document.getElementById("mainDiv"))

root.render(
    <App />
)