// The state for each cell element.
export type StateCell = 
    | 'available'
    | 'used'

// Board bidimensional
export type Board = Array<Array<StateCell>>

// Orientation that a ship can be
export type Orientation = 
    | 'HORIZONTAL' 
    | 'VERTICAL'

// Ship containing all the important information
export type Ship = {
    type: string,
    size: number,
    orientation: Orientation,
    col: number,
    row: number,
}

// Fleet containing all the ships 
export type Fleet = Array<Ship>

// Actions that can occur in fleet
export type ActionFleet = {
    type: 'rotation',
    ship: Ship,
    newOrientation: Orientation
} | {
    type: 'place',
    ship: Ship,
    col: number,
    row: number,
}

// Actions that can occur in BoardShips
export type ActionBoard = {
    type: 'shoted',
    col: number,
    row: number,
}

// Actions that can occur in BoardShots
export type ActionShot = {
    type: 'shot',
    col: number,
    row: number,
}

// Model used to make a setup fleet to API
export type SetupFleetModel = {
    ships: Array<ShipModel>
}

// Model used to make a shot to API
export type ShotModel = {
    gameId: String,
    position: PositionModel
}

// Status of game
export type GameStatus = "SETUP" | "YOUR_SHOT" | "OPPONENT_SHOT" | "YOU_WON" | "YOU_LOST"

// Model used by API to responde with game
export type GameModel = {
    myShips: Array<ShipModel>,
    myShots: Array<PositionModel>,
    opponentShots: Array<PositionModel>
}

// Model used by API to responde ship
export type ShipModel = {
    type: string,
    position: PositionModel,
    orientation: Orientation
}

// Model udes by API to responde position
export type PositionModel = {
    col: number,
    row: number
}
