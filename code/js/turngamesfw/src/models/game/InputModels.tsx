interface GameInputModel {
    name: string
    numPlayers: number
    description: string
    rules: string
}

const enum MatchState {
    SETUP = "SETUP",
    ON_GOING = "ON_GOING",
    FINISHED = "FINISHED",
}

interface MatchInputModel {
    id: string //UUID
    gameName: string
    state: MatchState
    players: number[] //Ids of players
    currPlayer: number //Id of current player
    currTurn: number
    deadlineTurn: Date
    created: Date
    info: string //JsonNode
}