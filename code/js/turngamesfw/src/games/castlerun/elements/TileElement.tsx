import * as React from 'react';
import { useEffect, useState } from 'react';
import { Paper, Box } from '@mui/material';
import { ArrowForward, ArrowDownward } from '@mui/icons-material';
import { Tile } from '../domain/Tile';

export function TileElement(props: { tile: Tile; onClick?: () => void }) {
    const { tile, onClick } = props;

    const [highlightStyle, setHighlightStyle] = useState<React.CSSProperties>({
        position: 'absolute',
        width: 'calc(100%)',
        height: 'calc(100%)',
        boxSizing: 'border-box',
    });

    useEffect(() => {
        setHighlightStyle({
            ...highlightStyle,
            border: tile.highlight ? '3px solid ' + tile.highlight : 'none',
            background: tile.type === Tile.Type.Wall ? tile.highlight || 'grey' : 'white',
        });
    }, [tile.highlight, tile.type]);

    const tileStyle: React.CSSProperties = {
        width: 35,
        height: 35,
        border: tile.highlight === null ? '1px solid black' : '1px solid ' + tile.highlight,
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        position: 'relative',
        cursor: tile.highlight ? 'pointer' : 'default',
        borderRadius: 0,
    };

    return (
        <Paper style={tileStyle} onClick={tile.highlight ? onClick : null}>
            <Box style={highlightStyle} />
            {renderArrow(tile)}
        </Paper>
    );
}


const renderArrow = (tile: Tile) => {
    const arrowContainerStyle: React.CSSProperties = {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: "translate(-50%, -50%)",
    };

    const arrowStyle: React.CSSProperties = {
        transform: tile.type === Tile.Type.EntranceB ? 'rotate(180deg)' : 'none',
    };

    if (tile.type === Tile.Type.Exit && tile.piece === null) {
        return (
            <Box style={arrowContainerStyle}>
                <ArrowForward style={arrowStyle} />
            </Box>
        );
    } else if (tile.type === Tile.Type.EntranceA || tile.type === Tile.Type.EntranceB) {
        return (
            <Box style={arrowContainerStyle}>
                <ArrowDownward style={arrowStyle} />
            </Box>
        );
    }
    return null;
};