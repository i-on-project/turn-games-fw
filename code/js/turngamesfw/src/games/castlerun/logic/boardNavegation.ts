import { Board } from "../domain/Board";
import { Coords } from "../domain/Coords";
import { Direction } from "../domain/Coords";
import { Equipment } from "../domain/Equipment";
import { Piece } from "../domain/Piece";
import { Tile } from "../domain/Tile";
import { Move } from "../domain/CastleRunMatch";

function getNeighbours(board: Board, position: Coords): Coords[] {
    const neighbours: Coords[] = [];

    if (position.row > 0)
        neighbours.push(position.getNext(Direction.Up));
    if (position.row < board.numRows - 1)
        neighbours.push(position.getNext(Direction.Down));
    if (position.col > 0)   
        neighbours.push(position.getNext(Direction.Left));
    if (position.col < board.numCols - 1)
        neighbours.push(position.getNext(Direction.Right));

    return neighbours.filter(c => {
        const tile = board.getTile(c);
        return tile.type !== Tile.Type.Wall &&
            tile.type !== Tile.Type.EntranceA &&
            tile.type !== Tile.Type.EntranceB;
    });
}

function possibleTiles(board: Board, start: Coords, distance: number): Coords[] {
    const possibleTiles: Coords[] = [];
    const visited: [Coords, number][] = [];
    const queue: [Coords, number][] = [];

    queue.push([start, 0]);

    while (queue.length > 0) {
        const [currTile, currDistance] = queue.shift()!;
        
        if (visited.some(([v, s]) => v === currTile && s <= currDistance) || possibleTiles.some(c => c.equals(currTile)))
            continue;
        
        visited.push([currTile, currDistance]);

        if (currDistance === distance || board.getTile(currTile).type === Tile.Type.Exit) {
            console.log(`Found tile: ${currTile.row},${currTile.col}`);
            possibleTiles.push(currTile);
        }
        else
            getNeighbours(board, currTile).forEach(n => {
                if (!visited.some(([v, s]) => v.equals(n) && s <= currDistance + 1))
                    queue.push([n, currDistance + 1]);
            });
    }

    return possibleTiles;
}

export function findPossibleMoves(board: Board, player: number, distance: number): Move[] {
    const list: Move[] = [];

    const entranceType = player === board.playerA ? Tile.Type.EntranceA : Tile.Type.EntranceB;
    const entrance = board.tiles.find(tile => tile.type === entranceType)!.coords;

    const deployTiles = possibleTiles(board, entrance, distance);

    deployTiles
        .filter(c => filter(board.getTile(c), player))
        .forEach(c => list.push({ piece: null, to: c, distance: distance }));

    const pieces = board.tiles.filter(tile => tile.piece?.owner === player && tile.type !== Tile.Type.Exit);
    pieces.forEach(p => {
        const possibleCoords = possibleTiles(board, p.coords, distance);
        possibleCoords
            .filter(c => filter(board.getTile(c), player))
            .forEach(c => list.push({ piece: p.piece, to: c, distance: distance }));
    });

    return list;
}

function filter(tile: Tile, player: number): boolean {
    if (tile.piece === null)
        return true;
    if (tile.type === Tile.Type.Exit)
        return false;
    return tile.piece.owner !== player || tile.piece.equipment?.type !== Equipment.Type.Shield || !tile.piece.equipment.upgraded;
}

function getFirstAvailablePosition(board: Board, start: Coords, spaces: number, direction: Direction): Coords {
    const next = start.getNext(direction);
    const tile = board.getTile(next);

    if (tile === null || tile.piece !== null) {
        if (spaces === 0) return start;
        return getFirstAvailablePosition(board, next, spaces - 1, direction);
    } else {
        return getFirstAvailablePosition(board, next, spaces, direction);
    }
}
