import * as React from 'react';
import { useEffect, useReducer } from 'react';

import Box from '@mui/material/Box';

import { TicTacToeLogic, TicTacToeMatch } from './TicTacToeLogic';

type ActionMatch = {
	type: 'play',
	row: number,
	col: number,
} | {
	type: 'update',
	match: TicTacToeMatch
}

function reduceMatch(state: TicTacToeMatch, action: ActionMatch): Match {
	switch (action.type) {
		case 'play': return TicTacToeLogic.newTurn(state, action.row, action.col)
		case 'update': return action.match
	}
}

export function TicTacToeBoard(props: { match: Match, playerId: number, onMatchUpdate: (match: Match) => void, doAction: (action: any) => void }) {
	const [match, dispatchMatch] = useReducer(reduceMatch, props.match as TicTacToeMatch)

	const handleClick = (row: number, col: number) => {
		dispatchMatch({
			type: 'play',
			row: row, 
			col: col
		})

		props.doAction({
			col: col, 
			row: row
		})
	};

	useEffect(() => {
		dispatchMatch({
			type: 'update',
			match: props.match
		})
	}, [props.match])

	useEffect(() => {
		props.onMatchUpdate(match)
	}, [match]);

	return (
		<Box sx={{ display: 'flex', flexWrap: 'wrap', width: '302px', margin: '0 auto', marginTop: '40px', border: '1px solid black'}}>
			{match.info.cells.map((row, rowIndex) => (
				row.map((value, colIndex) => {
					const index = (rowIndex * match.info.cells.length) + (colIndex + 1);

					return <Square
						key={index}
						value={value}
						onClick={() => handleClick(rowIndex, colIndex)}
						disabled={value != "EMPTY" || match.currPlayer != props.playerId || match.state == MatchState.FINISHED}
					/>
				})
				
			))}
		</Box>
	);
}

import { Clear, RadioButtonUnchecked } from '@mui/icons-material';

function Square(props: { value: string, onClick: () => any, disabled: boolean }) {
	const IconComponent = props.value === 'PLAYER_X' ? Clear : props.value === 'PLAYER_O' ? RadioButtonUnchecked : null;
	const iconColor = props.value === 'PLAYER_X' ? 'red' : props.value === 'PLAYER_O' ? 'blue' : 'inherit';

	return (
		<Box
			className="Square"
			sx={{
				width: '100px',
				height: '100px',
				border: '1px solid black',
				display: 'flex',
				justifyContent: 'center',
				alignItems: 'center',
				fontSize: '24px',
				cursor: props.disabled ? 'default' : 'pointer',
			}}

			onClick={!props.disabled ? props.onClick : undefined}
		>
			{IconComponent && <IconComponent sx={{ fontSize: '48px', color: iconColor }} />}
		</Box>
	);
}
