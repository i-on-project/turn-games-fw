import * as React from 'react';
import { useState } from 'react';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import { TicTacToeBoard } from '../tictactoe/TicTacToeElement';
import { TicTacToeMatch } from '../tictactoe/TicTacToeLogic';
import { useTimer } from './Timer';

export function MatchLayout(props: { match: Match, content }) {
  const [currentMatch, setCurrentMatch] = useState(props.match);

  const handleMatchChange = (updatedMatch) => {
    console.log('MatchLayout.handleMatchChange', updatedMatch);
    setCurrentMatch(updatedMatch);
  };

  const players = [
    { id: 0, name: 'User A' },
    { id: 1, name: 'User B' },
  ];

  return (
    <Container>
      <MatchStatus
        gameName={currentMatch.gameName}
        currTurn={currentMatch.currTurn}
        deadlineTurn={currentMatch.deadlineTurn}
        players={players}
        currPlayer={currentMatch.currPlayer}
      />
      {React.cloneElement(props.content, { match: currentMatch, onMatchUpdate: handleMatchChange })}
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
  const timer = useTimer(status.deadlineTurn);
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
            playerName={status.players[0].name}
            isCurrentPlayer={currPlayer.id === status.players[0].id}
            position={'left'}
          />
        </Box>

        <Box sx={{ flexGrow: 1, textAlign: 'center' }}>
          <Typography variant="h5" style={{ fontWeight: 'bold' }}>
            Turn: {status.currTurn}
          </Typography>
          <Box className={currPlayer.id === 0 ? 'TimerContainer CurrentPlayer' : 'TimerContainer'}>
            <Typography variant="h5" style={{ fontWeight: 'bold' }}>
              {timer.min}:{timer.sec}
            </Typography>
          </Box>
        </Box>

        <Box sx={{ width: '33%' }}>
          <StyledPlayer
            playerName={status.players[1].name}
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
      match={TicTacToeMatch}
      content={<TicTacToeBoard match={TicTacToeMatch} onMatchUpdate={(match: Match) => {}} />}
    />
  );
}
  
