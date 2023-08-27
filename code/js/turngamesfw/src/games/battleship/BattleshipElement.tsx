import * as React from "react"
import { useReducer, useEffect } from "react"

export const BOARD_SIZE = 10

import * as GameType from './typesGame'
import * as GameStyle from './stylesGame'
import { BattleshipLogic, BattleshipMatch } from "./BattleshipLogic"

import { Box, Button, Typography } from "@mui/material"

const initialStateFleet: GameType.Fleet = [
    {type: "CARRIER", size: 5, orientation: 'HORIZONTAL', col: null, row: null},
    {type: "BATTLESHIP", size: 4, orientation: 'HORIZONTAL', col: null, row: null},
    {type: "CRUISER", size: 3, orientation: 'HORIZONTAL', col: null, row: null},
    {type: "SUBMARINE", size: 3, orientation: 'HORIZONTAL', col: null, row: null},
    {type: "DESTROYER", size: 2, orientation: 'HORIZONTAL', col: null, row: null},
]

function reduceFleet(state: GameType.Fleet, action: GameType.ActionFleet): GameType.Fleet {
    switch (action.type) {
        case 'rotation':
            // TODO: Check when on board if can rotate
            return state.map((ship) => ship.type == action.ship.type ? {...ship, orientation: action.newOrientation} : ship)

        case 'place':
            // Check if can be placed
            if (action.col < 0 || action.row < 0 || 
                (action.ship.orientation == "HORIZONTAL" && action.col + action.ship.size > BOARD_SIZE) || 
                (action.ship.orientation == "VERTICAL" && action.row + action.ship.size > BOARD_SIZE)) {
                // TODO: Improve message out of bounds
                console.log("Out of bounds!")
                return state
            }

            // Move the ship element to position in board
            const shipElement = document.getElementById(action.ship.type)
            document.getElementById("BShip" + (action.row * BOARD_SIZE + action.col)).appendChild(shipElement)

            return state.map((ship) => ship.type == action.ship.type ? {...ship, col: action.col, row: action.row} : ship)
        
        default:
            // Unknown action, type system ensures this cannot happen
            return state
    }
}


function setupFleetModel(fleet: GameType.Fleet): GameType.SetupFleetModel {
    const fleetModel = fleet.map(ship => {return {type: ship.type, position: {col: ship.col, row: ship.row}, orientation: ship.orientation}})
    return {
        ships: fleetModel
    }
}

function handleDragStart(event: React.DragEvent<HTMLDivElement>) {
    event.dataTransfer.effectAllowed = "all"

    const ship = event.currentTarget.getAttribute('data-ship')
    event.dataTransfer.setData("ship", ship)

    const rect = event.currentTarget.getBoundingClientRect()
    const x = event.clientX - rect.left
    const y = event.clientY - rect.top
    event.dataTransfer.setData("mouse/x", x.toString())
    event.dataTransfer.setData("mouse/y", y.toString())
}

// Handle drap over to allow drop
function handleDragOver(event: React.DragEvent<HTMLDivElement>) {
    event.preventDefault()
}

function updateFleet (myShips: Array<GameType.ShipModel>): GameType.Fleet {
    return initialStateFleet.map((ship: GameType.Ship) => {
        myShips.forEach((shipModel: GameType.ShipModel) => {
            if (ship.type == shipModel.type) {
                ship = {...ship, orientation: shipModel.orientation, col: shipModel.position.col, row: shipModel.position.row}
            }
        })
        return ship
    })
}

type ActionMatch = {
	type: 'setup',
	fleet: Array<GameType.ShipModel>,
} | {
	type: 'play',
	row: number,
	col: number,
} | {
	type: 'update',
	match: BattleshipMatch
}

function reduceMatch(state: BattleshipMatch, action: ActionMatch): Match {
	switch (action.type) {
        case 'setup': return BattleshipLogic.setup(state, action.fleet)
		case 'play': return BattleshipLogic.newTurn(state, action.row, action.col)
		case 'update': return action.match
	}
}

export function BattleshipBoard(props: { match: Match, playerId: number, onMatchUpdate: (match: Match) => void, setupAction: (action: any) => void, doAction: (action: any) => void }) {
    const [match, dispatchMatch] = useReducer(reduceMatch, props.match as BattleshipMatch)
    const [fleet, dispatchFleet] = useReducer(reduceFleet, match.info.myBoard.boardShips.ships.length == 0 ? initialStateFleet : updateFleet(match.info.myBoard.boardShips.ships))

    console.log(match)

    const status = match.state
    const waiting = (match.state == MatchState.SETUP && match.info.myBoard.boardShips.ships.length != 0) || (match.state != MatchState.SETUP && match.currPlayer != props.playerId)

    function handleRotation(ship: GameType.Ship) {
        const newOrientation = ship.orientation == 'HORIZONTAL' ? 'VERTICAL' : 'HORIZONTAL'

        dispatchFleet({
            type: 'rotation',
            ship: ship,
            newOrientation: newOrientation
        })
    }

    // Handler for the `drop` event, which dispatches a `drop` event to the reducer managing state.
    function handleDrop(event: React.DragEvent<HTMLDivElement>) {
        if (event.dataTransfer.getData("ship") == "")
            return

        // Collect information about the source
        const ship = JSON.parse(event.dataTransfer.getData("ship")) as GameType.Ship
        const x = parseInt(event.dataTransfer.getData("mouse/x"))
        const y = parseInt(event.dataTransfer.getData("mouse/y"))

        // Collect information about the target
        const col = parseInt(event.currentTarget.getAttribute('data-col'))
        const row = parseInt(event.currentTarget.getAttribute('data-row'))

        const minusCol = Math.floor(x / (GameStyle.CELL_SIZE + GameStyle.CELL_BOARDER_SIZE * 2))
        const minusRow = Math.floor(y / (GameStyle.CELL_SIZE + GameStyle.CELL_BOARDER_SIZE * 2))
        
        dispatchFleet({
            type: 'place',
            ship: ship,
            col: col - minusCol,
            row: row - minusRow,
        })
    }

    async function handleShot(event: React.MouseEvent<HTMLDivElement>) {
        // Collect information about the target
        const col = parseInt(event.currentTarget.getAttribute('data-col'))
        const row = parseInt(event.currentTarget.getAttribute('data-row'))

        dispatchMatch({
            type: 'play',
            col: col,
            row: row
        })

        props.doAction({
            col: col, 
			row: row
        })
    }

    async function handleSetup() {
        dispatchMatch({
            type: 'setup',
            fleet: setupFleetModel(fleet).ships
        })
        props.setupAction(
            setupFleetModel(fleet)
        )
    }

    useEffect(() => {
        fleet.forEach((ship) => {
            const shipElement = <div key={ship.type} style={GameStyle.shipContainer}>
                <div
                    id={ship.type}
                    style={GameStyle.shipStyle(ship)} 
                    draggable={status == "SETUP" && waiting == false ? true : false} 
                    onClick={status == "SETUP" && waiting == false ? () => handleRotation(ship) : undefined} 
                    onDragStart={status == "SETUP" && waiting == false ? handleDragStart : undefined}
                    data-ship={JSON.stringify(ship)}
                ></div>
            </div>

            if (ship.col != null || ship.row != null) {
                const shipElement = document.getElementById(ship.type)
                document.getElementById("BShip" + (ship.row * BOARD_SIZE + ship.col)).appendChild(shipElement)
            }
        })
    }, [])

    useEffect(() => {
		dispatchMatch({
			type: 'update',
			match: props.match
		})

	}, [props.match])

    return (
        <div>
            {status == "SETUP" && 
            <Box sx={{ textAlign: 'center', marginTop: 4, marginBottom: 4 }}>
                <Button variant="contained" onClick={handleSetup} disabled={status != "SETUP" || waiting ? true : false}>
                    {waiting ? 'WAITING' : 'SETUP DONE'}
                </Button>
            </Box>}
            <Box sx={{ textAlign: 'center', width: 1, marginBottom: 5 }}>
                <Typography variant="h5" className="BoardshipsName">
                    BoardShips
                </Typography>
            </Box>
            <div style={GameStyle.boardStyle}>
                {match.info.myBoard.boardShips.grid.map((grid, row) => grid.map((state, col) => <div
                        id={"BShip" + (row * BOARD_SIZE + col)}
                        key={"BShip" + (row * BOARD_SIZE + col)}
                        style={GameStyle.cellStyle(col + 1, row + 1)} 
                        onDrop={status == "SETUP" && waiting == false ? handleDrop : undefined}
                        onDragOver={status == "SETUP" && waiting == false ? handleDragOver : undefined}
                        data-col={col} 
                        data-row={row}
                        draggable={false}
                    >{match.info.myBoard.boardShots.grid[row][col] == true && <span draggable={false}>X</span>}</div>))}
            </div>
            <Box sx={{ textAlign: 'center', width: 1, marginBottom: 4, marginTop: 4 }}>
                <Typography variant="h5" className="BoardshipsName">
                    {status == "SETUP" && 'Fleet'}
                </Typography>
            </Box>
            <div style={GameStyle.fleetStyle}>
                {fleet.map((ship) => 
                    <div key={ship.type} style={GameStyle.shipContainer}>
                        <div
                            id={ship.type}
                            style={GameStyle.shipStyle(ship)} 
                            draggable={status == "SETUP" && waiting == false ? true : false} 
                            onClick={status == "SETUP" && waiting == false ? () => handleRotation(ship) : undefined} 
                            onDragStart={status == "SETUP" && waiting == false ? handleDragStart : undefined}
                            data-ship={JSON.stringify(ship)}
                        ></div>
                    </div>
                )}
            </div>
            {status != "SETUP" &&
            <Box sx={{ textAlign: 'center', width: 1, marginBottom: 5 }}>
                <Typography variant="h5" className="BoardshotsName">
                    BoardShots
                </Typography>
            </Box>}
            {status != "SETUP" && <div style={GameStyle.boardStyle}>
                {match.info.myShots.grid.map((grid, row) => grid.map((state, col) => <div
                        id={"BShot" + (row * BOARD_SIZE + col)}
                        key={"BShot" + (row * BOARD_SIZE + col)}
                        style={{...GameStyle.cellStyle(col + 1, row + 1), ...GameStyle.cellShotStyle(state)}} 
                        data-col={col} 
                        data-row={row}
                        onClick={status == MatchState.ON_GOING && waiting == false && !state ? handleShot : undefined}
                    ></div>))}
            </div>}
        </div>
    )
}