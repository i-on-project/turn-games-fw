import * as React from 'react'
import {
    createContext,
    useContext,
} from 'react'

type GameProps = {
    match: Match
    playerId: number
    doSetup: (action: any) => void
    doTurn: (action: any) => void
}

type ContextType = {
    components: Map<string, React.FC<GameProps>>
}

const GamesContext = createContext<ContextType>({
    components: undefined
})

export function GamesProvider({ children, gamesComponents }: { children: React.ReactNode, gamesComponents: Map<string, React.FC<GameProps>> }) {
    return (
        <GamesContext.Provider value={{components: gamesComponents}}>
            {children}
        </GamesContext.Provider>
    )
}

export function useComponents() {
    return useContext(GamesContext).components
}

export function useComponent(name: string) {
    return useContext(GamesContext).components.get(name)
}