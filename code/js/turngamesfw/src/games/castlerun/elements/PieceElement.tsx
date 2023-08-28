import * as React from 'react';
import { Paper, SvgIcon } from '@mui/material';
import { Piece } from '../domain/Piece';

interface PieceElementProps {
    piece: Piece;
    color: 'red' | 'blue';
}

export function PieceElement(props: PieceElementProps) {
    const { piece, color } = props;

    const pieceStyle: React.CSSProperties = {
        width: 20,
        height: 20,
        borderRadius: '50%',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        position: 'relative',
        backgroundColor: color,
        color: 'white',
    };

    const crownStyle: React.CSSProperties = {
        position: 'absolute',
        top: -10,
        left: 7,
    };

    return (
        <Paper elevation={0} style={pieceStyle}>
            {piece.isKing ? (
                <SvgIcon style={crownStyle}>
                    <path
                        d="M2.5 12.5L5 6H2L4 3H9L11 6H8L10 12.5H9.5L8.5 10H5.5L4.5 12.5H4Z"
                        fill="yellow"
                    />
                </SvgIcon>
            ) : null}
        </Paper>
    );
}
