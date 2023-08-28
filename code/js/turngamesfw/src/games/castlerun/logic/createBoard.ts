import { Coords } from "../domain/Coords";
import { Board } from "../domain/Board";
import { Tile } from "../domain/Tile";
import { PiecesLeft } from "../domain/PiecesLeft";

export function newBoard(
    playerA: number,
    playerB: number,
    numRows: number = 9,
    numCols: number = 36,
    numPieces: number = 7,
    walls: Coords[] = DefaultLayout.walls,
    entranceA: Coords = DefaultLayout.entranceA,
    entranceB: Coords = DefaultLayout.entranceB,
    exits: Coords[] = DefaultLayout.exits
) {
    function createTiles(numRows: number, numCols: number, walls: Coords[], exits: Coords[], entranceA: Coords, entranceB: Coords): Tile[] {
        const tiles: Tile[] = [];
        for (let row = 0; row < numRows; row++) {
            for (let col = 0; col < numCols; col++) {
                const coords = new Coords(row, col);
                const tile = new Tile(coords);
                tiles.push(tile);
            }
        }

        const entranceATile = tiles[entranceA.row * numCols + entranceA.col];
        entranceATile.type = Tile.Type.EntranceA;

        const entranceBTile = tiles[entranceB.row * numCols + entranceB.col];
        entranceBTile.type = Tile.Type.EntranceB;

        walls.forEach(c => tiles[c.row * numCols + c.col].type = Tile.Type.Wall);
        exits.forEach(c => tiles[c.row * numCols + c.col].type = Tile.Type.Exit);

        const numEquipmentTiles = Math.floor(numRows * numCols / 10);
        for (let i = 0; i < numEquipmentTiles; i++) {
            const randomTile = tiles[Math.floor(Math.random() * tiles.length)];
            if (randomTile.type === Tile.Type.Floor) {
                randomTile.type = Tile.Type.Equipment;
            }
        }

        return tiles;
    }

    const tiles = createTiles(numRows, numCols, walls, exits, entranceA, entranceB);

    return new Board(
        playerA,
        playerB,
        numRows,
        numCols,
        numPieces,
        new PiecesLeft(playerA, playerB, numPieces, numPieces),
        tiles
    );
}

class DefaultLayout {
    static walls: Coords[] = [
        new Coords(0, 1), new Coords(1, 1), new Coords(2, 1), new Coords(3, 1), new Coords(5, 1), new Coords(6, 1), new Coords(7, 1), new Coords(8, 1),
        new Coords(7, 2), new Coords(8, 2),
        new Coords(1, 3), new Coords(2, 3), new Coords(4, 3), new Coords(5, 3), new Coords(7, 3), new Coords(8, 3),
        new Coords(2, 4), new Coords(4, 4), new Coords(8, 4),
        new Coords(0, 5), new Coords(6, 5),
        new Coords(0, 6), new Coords(1, 6), new Coords(3, 6), new Coords(4, 6), new Coords(5, 6), new Coords(6, 6), new Coords(7, 6),
        new Coords(6, 7),
        new Coords(1, 8), new Coords(3, 8), new Coords(4, 8), new Coords(8, 8),
        new Coords(1, 9), new Coords(5, 9), new Coords(6, 9), new Coords(8, 9),
        new Coords(1, 10), new Coords(2, 10), new Coords(3, 10), new Coords(5, 10),
        new Coords(3, 11), new Coords(7, 11),
        new Coords(0, 12), new Coords(1, 12), new Coords(3, 12), new Coords(4, 12), new Coords(6, 12), new Coords(7, 12),
        new Coords(3, 13), new Coords(4, 13), new Coords(6, 13),
        new Coords(1, 14), new Coords(8, 14),
        new Coords(1, 15), new Coords(2, 15), new Coords(3, 15), new Coords(4, 15), new Coords(5, 15), new Coords(6, 15), new Coords(8, 15),
        new Coords(8, 16),
        new Coords(0, 17), new Coords(1, 17), new Coords(2, 17), new Coords(3, 17), new Coords(5, 17), new Coords(6, 17), new Coords(7, 17), new Coords(8, 17),
        new Coords(0, 18), new Coords(1, 18), new Coords(2, 18), new Coords(3, 18), new Coords(5, 18), new Coords(6, 18), new Coords(7, 18), new Coords(8, 18),

        new Coords(1, 20), new Coords(2, 20), new Coords(3, 20), new Coords(5, 20), new Coords(6, 20), new Coords(7, 20),
        new Coords(2, 21), new Coords(3, 21), new Coords(7, 21),
        new Coords(0, 22), new Coords(3, 22), new Coords(5, 22),
        new Coords(0, 23), new Coords(1, 23), new Coords(5, 23), new Coords(6, 23), new Coords(8, 23),
        new Coords(3, 24), new Coords(4, 24), new Coords(8, 24),
        new Coords(1, 25), new Coords(3, 25), new Coords(6, 25),
        new Coords(1, 26), new Coords(5, 26), new Coords(6, 26), new Coords(7, 26),
        new Coords(3, 27),
        new Coords(0, 28), new Coords(2, 28), new Coords(3, 28), new Coords(4, 28), new Coords(6, 28), new Coords(7, 28), new Coords(8, 28),
        new Coords(0, 29), new Coords(3, 29), new Coords(4, 29), new Coords(8, 29),
        new Coords(0, 30), new Coords(1, 30), new Coords(3, 30), new Coords(6, 30), new Coords(8, 30),
        new Coords(0, 31), new Coords(5, 31), new Coords(6, 31), new Coords(8, 31),
        new Coords(0, 32), new Coords(2, 32), new Coords(3, 32),
        new Coords(3, 33), new Coords(4, 33), new Coords(5, 33), new Coords(7, 33),
        new Coords(1, 34), new Coords(3, 34), new Coords(7, 34),
        new Coords(1, 35), new Coords(3, 35), new Coords(5, 35), new Coords(7, 35)
    ];
    static entranceA: Coords = new Coords(0, 0);
    static entranceB: Coords = new Coords(8, 0);
    static exits: Coords[] = [
        new Coords(0, 35),
        new Coords(2, 35),
        new Coords(4, 35),
        new Coords(6, 35),
        new Coords(8, 35)
    ];
}
