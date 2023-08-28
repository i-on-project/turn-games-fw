import * as React from 'react';
import { Paper, Grid } from '@mui/material';
import { PieceElement } from './PieceElement';
import { Piece } from '../domain/Piece';

interface PiecesLeftElementProps {
    piecesLeft: number;
    player: number;
    color: 'red' | 'blue';
}

export function PiecesLeftElement(props: PiecesLeftElementProps) {
    const { piecesLeft, player, color } = props;
    const numPieces = piecesLeft;

    const containerStyle: React.CSSProperties = {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        margin: '3px',
        marginLeft: '215px',
    };

    const piecesContainerStyle: React.CSSProperties = {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
    };

    const pieces: JSX.Element[] = [];
    for (let i = 0; i < numPieces; i++) {
        pieces.push(
            <PieceElement key={i} piece={new Piece(player, null)} color={color} />
        );
    }

    return (
        <Paper elevation={3} style={containerStyle}>
            <Grid container style={piecesContainerStyle}>
                {pieces}
            </Grid>
        </Paper>
    );
}
