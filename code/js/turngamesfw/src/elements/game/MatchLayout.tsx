import * as React from 'react';
import { useState, useEffect } from 'react';
import { useLoaderData, useParams } from 'react-router-dom';
import { useCookies } from 'react-cookie';

import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';

import { fetchAPI } from '../../utils/fetchApi';
import { useTimer } from '../../utils/timer';
import { TurnInputModel } from '../../models/game/OutputModels';

import { useComponent } from '../../utils/GamesContext';

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
	const { gameName, matchId } = useParams()

	const {match, players} = useLoaderData() as {match: Match, players: User[]}

	const [cookies, setCookie, removeCookie] = useCookies(["login"])
	const playerId = players.find(player => player.username == cookies.login.username).id

	const [currentMatch, setCurrentMatch] = useState(match);
	const [waiting, setWaiting] = useState(match.state == MatchState.ON_GOING && match.currPlayer != playerId ? true : false)

	const handleMatchChange = (updatedMatch: Match) => {
		setCurrentMatch(updatedMatch)
	}

	const setupAction = async (action: any) => {
		const body = new TurnInputModel(matchId, action)

		const resp = await fetchAPI("/api/game/" + gameName + "/setup", "POST", body, false)
		if (resp.status != 200) {
			// TODO: Error catching
			return
		}

		setWaiting(true)
	}

	const doAction = async (action: any) => {
		const body = new TurnInputModel(matchId, action)

		const resp = await fetchAPI("/api/game/" + gameName + "/turn", "POST", body, false)
		if (resp.status != 200) {
			// TODO: Error catching
			return
		}

		setWaiting(true)
	}

	async function getState(intervalId: number) {
        const resp = await fetchAPI("/api/game/" + gameName + "/" + matchId + "/myturn", "GET")
		if (resp.body.gameOver == true) {
			clearInterval(intervalId)
			setWaiting(false)
			return
		}

		if (resp.body.myTurn) {
			clearInterval(intervalId)
			setWaiting(false)
		}

		if (currentMatch.state == MatchState.SETUP && resp.body.setup == false) {
			clearInterval(intervalId)
			setWaiting(false)
		}
    }

	async function getUpdatedMatch() {
		const resp = await fetchAPI("/api/game/" + gameName + "/match/" + matchId, "GET")
		const updatedMatch = resp.body["properties"] as Match

        setCurrentMatch(updatedMatch)
		setWaiting(updatedMatch.state == MatchState.ON_GOING && updatedMatch.currPlayer != playerId)
    }

	useEffect(() => {
        if (waiting) {
            const interval = setInterval(() => getState(interval), 1000)
        } else {
            getUpdatedMatch()
        }
    }, [waiting])

	const GameComponent = useComponent(gameName)

	return (
		<Container>
			<MatchStatus
				gameName={currentMatch.gameName}
				currTurn={currentMatch.currTurn}
				deadlineTurn={currentMatch.deadlineTurn}
				players={players}
				currPlayer={currentMatch.currPlayer}
				matchState={currentMatch.state}
			/>
			<GameComponent match={currentMatch} playerId={playerId} onMatchUpdate={handleMatchChange} setupAction={setupAction} doAction={doAction}/>
		</Container>
	);
}

function MatchStatus(status: {
	gameName: string;
	currTurn: number;
	deadlineTurn: Date;
	players: User[];
	currPlayer: number;
	matchState: MatchState
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
						isCurrentPlayer={currPlayer.id === status.players[0].id || status.matchState == MatchState.SETUP}
						position={'left'}
					/>
				</Box>
				
				<Box sx={{ flexGrow: 1, textAlign: 'center' }}>
					<Typography variant="h5" style={{ fontWeight: 'bold' }}>
						{status.matchState == MatchState.SETUP ? "Setup" : "Turn: " + status.currTurn}
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
						isCurrentPlayer={currPlayer.id === status.players[1].id || status.matchState == MatchState.SETUP}
						position={'right'}
					/>
				</Box>

			</Box>
			{status.matchState == MatchState.FINISHED && <Box display="flex" mt="15px">
				<Box sx={{ flexGrow: 1, textAlign: 'center' }}>
					<Typography variant="h5" style={{ fontWeight: 'bold' }}>
						GAME OVER!
					</Typography>
				</Box>
			</Box>}
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
