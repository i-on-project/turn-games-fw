import * as React from 'react';
import { Grid, Container } from '@mui/material';
import { TileElement } from './TileElement';
import { Board } from '../domain/Board';
import { Tile } from '../domain/Tile';

export function BoardElement(props: { board: Board, onSelectTile: (tile: Tile) => void }) {
    const { board, onSelectTile} = props;

    const numRows = board.numRows;
    const numCols = board.numCols;

    const tileElements = [];

    for (let col = 0; col < numCols; col++) {
        const colTiles = [];

        for (let row = 0; row < numRows; row++) {
            const index = row * numCols + col;
            const tile = board.tiles[index];

            colTiles.push(
                <Grid key={index}>
                    <TileElement 
                        tile={tile} 
                        onClick={() => onSelectTile(tile)} 
                        pieceColor={tile.piece ? tile.piece.owner === board.alpha ? 'red' : 'blue' : null}
                    />
                </Grid>
            );
        }

        tileElements.push(
            <Grid key={col}>{colTiles}</Grid>
        );
    }

    const gridContainerStyle = {
        display: 'flex',
        justifyContent: 'center',
    };

    return (
        <Grid container spacing={0} style={gridContainerStyle}>
            {tileElements}
        </Grid>
    );
}
