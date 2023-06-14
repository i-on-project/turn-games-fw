import * as React from 'react';
import { useState, useEffect } from 'react';
import { useLoaderData } from 'react-router-dom';

import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';

import { fetchAPI } from '../../utils/fetchApi';
import { useTimer } from './Timer';

import { TicTacToeBoard } from '../tictactoe/TicTacToeElement';
import { TicTacToeMatch } from '../tictactoe/TicTacToeLogic';

async function getPlayerInfo(playersId: number[]): Promise<User[]> {
	const tempPlayers = []
	for (const playerId of playersId) {
		const resp = await fetchAPI("/api/user/" + playerId, "GET")
		const player: User = resp.body["properties"] as User
		tempPlayers.push(player)
	}
	return tempPlayers
}

export async function loadMatchLayout({params}) {

	const resp = await fetchAPI("/api/game/" + params.gameName + "/match/" + params.matchId, "GET")

	const match = resp.body["properties"] as Match

	const players = await getPlayerInfo(match.players)

	return {match: match, players: players}
}

export function MatchLayout() {
	const {match, players} = useLoaderData() as {match: Match, players: User[]}

	const [currentMatch, setCurrentMatch] = useState(match);

	const handleMatchChange = (updatedMatch) => {
		console.log('MatchLayout.handleMatchChange', updatedMatch);
		setCurrentMatch(updatedMatch);
	};

	return (
		<Container>
			<MatchStatus
				gameName={currentMatch.gameName}
				currTurn={currentMatch.currTurn}
				deadlineTurn={currentMatch.deadlineTurn}
				players={players}
				currPlayer={currentMatch.currPlayer}
			/>
		</Container>
	);
}

function MatchStatus(status: {
	gameName: string;
	currTurn: number;
	deadlineTurn: Date;
	players: User[];
	currPlayer: number;
}) {

	let t = false
	let timer = {min: 0, sec: 0}

	if (status.deadlineTurn != undefined) {
		timer = useTimer(status.deadlineTurn);
		t = true
	}

	const currPlayer = status.players.find((player) => player.id === status.currPlayer);

	return (
		<Box width="1">
			<Box sx={{ textAlign: 'center', width: 1 }}>
				<Typography variant="h3" className="MatchStateGameName">
					{status.gameName}
				</Typography>
			</Box>

			<Box display="flex" mt="15px">
				<Box sx={{ width: '33%' }}>
					<StyledPlayer
						playerName={status.players[0].username}
						isCurrentPlayer={currPlayer.id === status.players[0].id}
						position={'left'}
					/>
				</Box>
				
				<Box sx={{ flexGrow: 1, textAlign: 'center' }}>
					<Typography variant="h5" style={{ fontWeight: 'bold' }}>
						Turn: {status.currTurn}
					</Typography>
					{ t == true &&
					<Box className={currPlayer.id === 0 ? 'TimerContainer CurrentPlayer' : 'TimerContainer'}>
						<Typography variant="h5" style={{ fontWeight: 'bold' }}>
							{timer.min}:{timer.sec}
						</Typography>
					</Box>
					}
				</Box>

				<Box sx={{ width: '33%' }}>
					<StyledPlayer
						playerName={status.players[1].username}
						isCurrentPlayer={currPlayer.id === status.players[1].id}
						position={'right'}
					/>
				</Box>
			</Box>
		</Box>
	);
}

function StyledPlayer({ playerName, isCurrentPlayer, position }) {
	const playerStyles = {
		background: isCurrentPlayer ? `linear-gradient(to ${position}, #ffffff, #00ff00)` : 'transparent',
		padding: '8px',
		borderRadius: '4px',
		textAlign: position,
	};

	return (
		<Box sx={playerStyles}>
			<Typography variant="h6" className="MatchStatePlayerName">
				{playerName}
			</Typography>
		</Box>
	);
}

export function MockMatchLayout() {
	return (
		<MatchLayout
			
		/>
	);
}

