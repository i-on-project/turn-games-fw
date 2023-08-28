import * as React from 'react';
import { Grid } from '@mui/material';
import { Paper } from '@mui/material';
import { Dices, DicePair } from '../domain/Dices';
import { Box } from '@mui/material';

interface DicesElementProps {
    dices: Dices;
    onPlayRoll: () => void;
    onDuelRoll: () => void;
}

export function DicesElement(props: DicesElementProps) {
    const { dices, onPlayRoll, onDuelRoll } = props;

    return (
        <Box sx={{ margin: '20px', marginLeft: '200px' }}>
            <Box sx={{ border: '3px solid black', p: '3px' }}>
                <DicePairElement pair={dices.play} type="play" onRoll={onPlayRoll}/>
                <DicePairElement pair={dices.duel} type="duel" onRoll={onDuelRoll}/>
            </Box>
        </Box>
    );
}

interface DicePairElementProps {
    pair: DicePair;
    type: 'play' | 'duel';
    onRoll: () => void;
}

function DicePairElement(props: DicePairElementProps) {
    const { pair, type, onRoll } = props;

    return (
        <Box sx={{ display: 'flex', marginTop: '20px', marginBottom: "20px"}} onClick={onRoll}>
            <DiceElement value={pair.dice1} color={type === 'play' ? 'white' : 'blue'} dotColor={type === 'play' ? 'black' : 'white'} active={pair.canRoll} />
            <DiceElement value={pair.dice2} color={type === 'play' ? 'white' : 'red'} dotColor={type === 'play' ? 'black' : 'white'} active={pair.canRoll}/>
        </Box>
    );
}

interface DiceProps {
    value: number;
    color: string;
    dotColor: string;
    active: boolean;
}

export function DiceElement(props: DiceProps) {
    const { value, color, dotColor, active } = props;

    const diceStyle: React.CSSProperties = {
        width: 150,
        height: 150,
        borderRadius: '8px',
        display: 'grid',
        gridTemplateColumns: 'repeat(3, 1fr)',
        gridTemplateRows: 'repeat(3, 1fr)',
        backgroundColor: color,
        marginLeft: '10px',
        marginRight: '10px',
        opacity: 1,
        cursor: active ? 'pointer' : 'default',
    };

    const dotStyle: React.CSSProperties = {
        width: '80%',
        height: '80%',
        borderRadius: '50%',
        backgroundColor: dotColor,
        marginTop: '5px',
        marginLeft: '5px',
    };

    const dotIndexes = {
        1: [4],
        2: [0, 8],
        3: [0, 4, 8],
        4: [0, 2, 6, 8],
        5: [0, 2, 4, 6, 8],
        6: [0, 2, 6, 3, 5, 8],
    };

    const dots = Array.from({ length: 9 }, (_, index) => (
        <div key={index} style={{ ...dotStyle, opacity: dotIndexes[value]?.includes(index) ? 1 : 0 }} />
    ));

    return (
        <Paper elevation={3} style={diceStyle}>
            {dots}
        </Paper>
    );
}