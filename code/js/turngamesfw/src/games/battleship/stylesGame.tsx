import * as GameType from './typesGame'

export const CELL_SIZE = 40
export const CELL_BOARDER_SIZE = 1

// The CSS style for the board wrapper div, in order to use CSS Grid.
export const boardStyle: React.CSSProperties = {
    display: "grid",
    gridTemplateColumns: "repeat(10, " + CELL_SIZE + "px)",
    gridTemplateRows: "repeat(10, " + CELL_SIZE + "px)",
    gap: CELL_BOARDER_SIZE + "px",
    padding: CELL_BOARDER_SIZE + "px",
    justifyContent: "center",
    alignContent: "center",
}

// The function to compute the CSS style for a cell in board
export function cellStyle(column: number, row: number): React.CSSProperties {
    return {
        position: "relative",
        gridColumn: column,
        gridRow: row,
        border: "2px solid",
        width: "100%",
        height: "100%",
        borderColor: "black",
        fontSize: "25px",
        textAlign: 'center',
    }
}

export function cellShotStyle(state: boolean): React.CSSProperties {
    return {
        backgroundColor: state ? 'red' : 'white',
    }
}

// The CSS style for wrapper of fleet to align divs to center
export const fleetStyle: React.CSSProperties = {
    position: "relative",
    textAlign: "center",
    verticalAlign: "middle",
}

// The CSS style for the wrapper of each ship to add distance between ships
export const shipContainer: React.CSSProperties = {
    display: "inline-block",
    verticalAlign: "middle",
    padding: "5px",
}

// Function to compute the CSS style for each ship
export function shipStyle(ship: GameType.Ship): React.CSSProperties {
    const horizontalMult = ship.orientation == 'HORIZONTAL' ? ship.size : 1
    const verticalMult = ship.orientation == 'VERTICAL' ? ship.size : 1

    return {
        position: ship.col == null && ship.row == null ? "relative" : "absolute",
        top: "-" + (CELL_BOARDER_SIZE + 1) + "px",
        left: "-" + (CELL_BOARDER_SIZE + 1) + "px",
        width: (CELL_SIZE * horizontalMult) + (CELL_BOARDER_SIZE * (horizontalMult - 1)) + "px", 
        height: (CELL_SIZE * verticalMult) + (CELL_BOARDER_SIZE * (verticalMult - 1)) + "px", 
        border: CELL_BOARDER_SIZE + 1 + "px solid black",
        backgroundColor: "rgba(0,0,0,0.5)",
        zIndex: "999"
    }
}