import { Board } from "../domain/Board";
import { Coords, getNext, equals } from "../domain/Coords";
import { Direction } from "../domain/Coords";
import { Piece } from "../domain/Piece";
import { Tile } from "../domain/Tile";
import { Move } from "../CastleRunMatch";
import { BoardLogic } from "../logic/boardLogic";

function getNeighbours(board: Board, position: Coords): Coords[] {
    const neighbours: Coords[] = [];
    
    if (position.row > 0)
        neighbours.push(getNext(position, Direction.Up));
    if (position.row < board.numRows - 1)
        neighbours.push(getNext(position, Direction.Down));
    if (position.col > 0)
        neighbours.push(getNext(position, Direction.Left));
    if (position.col < board.numCols - 1)
        neighbours.push(getNext(position, Direction.Right));

    return neighbours.filter(c => {
        const tile = BoardLogic.getTile(board, c);
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
        
        if (visited.some(([v, s]) => v === currTile && s <= currDistance) || possibleTiles.some(c => equals(c, currTile)))
            continue;
        
        visited.push([currTile, currDistance]);

        if (currDistance === distance || BoardLogic.getTile(board, currTile).type === Tile.Type.Exit) {
            possibleTiles.push(currTile);
        }
        else
            getNeighbours(board, currTile).forEach(n => {
                if (!visited.some(([v, s]) => equals(v, n) && s <= currDistance + 1))
                    queue.push([n, currDistance + 1]);
            });
    }

    return possibleTiles;
}

export function findPossibleMoves(board: Board, player: number, distance: number): Move[] {
    const list: Move[] = [];

    const entranceType = player === board.alpha ? Tile.Type.EntranceA : Tile.Type.EntranceB;
    const entrance = board.tiles.find(tile => tile.type === entranceType)!.coords;

    const deployTiles = possibleTiles(board, entrance, distance);

    deployTiles
        .filter(c => filter(BoardLogic.getTile(board, c), player))
        .forEach(c => list.push({ piece: null, to: c, distance: distance }));

    const pieces = board.tiles.filter(tile => tile.piece?.owner === player && tile.type !== Tile.Type.Exit);
    pieces.forEach(p => {
        const possibleCoords = possibleTiles(board, p.coords, distance);
        possibleCoords
            .filter(c => filter(BoardLogic.getTile(board, c), player))
            .forEach(c => list.push({ piece: p.piece, to: c, distance: distance }));
    });

    return list;
}

function filter(tile: Tile, player: number): boolean {
    if (tile.piece === null)
        return true;
    if (tile.type === Tile.Type.Exit)
        return false;
    return tile.piece.owner !== player;
}

function getFirstAvailablePosition(board: Board, start: Coords, spaces: number, direction: Direction): Coords {
    const next = getNext(start, direction);
    const tile = BoardLogic.getTile(board, next);

    if (tile === null || tile.piece !== null) {
        if (spaces === 0) return start;
        return getFirstAvailablePosition(board, next, spaces - 1, direction);
    } else {
        return getFirstAvailablePosition(board, next, spaces, direction);
    }
}
