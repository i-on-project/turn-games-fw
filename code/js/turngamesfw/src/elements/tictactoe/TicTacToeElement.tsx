import * as React from 'react';
import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import { TicTacToeLogic } from './TicTacToeLogic';
import Button from '@mui/material/Button';

export function TicTacToeBoard(props: { match: Match, onMatchUpdate: (match: Match) => void }) {
  const [game, setGame] = useState(props.match.info);

  const handleClick = (index) => {
    const updatedGame = TicTacToeLogic.newTurn(game, index);

    if (updatedGame == game) return;
    
    setGame(updatedGame);
    props.onMatchUpdate({ ...props.match, 
      currPlayer: props.match.currPlayer === 0 ? 1 : 0,
      currTurn: props.match.currTurn + 1,
      info: updatedGame 
    });
  };

  const handleReset = () => {
    const resetGame = TicTacToeLogic.resetGame();
    setGame(resetGame);
    props.onMatchUpdate({ ...props.match, info: resetGame });
  };

  useEffect(() => {
    setGame(props.match.info);
  }, [props.match.info]);

  useEffect(() => {
    // Update the game state when the match prop changes
    setGame(props.match.info);
  }, [props.match]);

  return (
    <Box sx={{ display: 'flex', flexWrap: 'wrap', width: '310px', margin: '0 auto', marginTop: '40px' }}>
      {game.squares.map((value, index) => (
        <Square
          key={index}
          value={value}
          onClick={() => handleClick(index)}
          disabled={value || game.gameOver}
          gameOver={game.gameOver}
        />
      ))}

      {game.gameOver && (
        <Box
          sx={{
            width: '100%',
            display: 'flex',
            justifyContent: 'center',
            marginTop: '16px',
          }}
        >
          <Button
            onClick={handleReset}
            variant="contained"
            color="primary"
            sx={{
              backgroundColor: '#ff4081',
              '&:hover': {
                backgroundColor: '#f50057',
              },
            }}
          >
            Reset Game
          </Button>
        </Box>
      )}
    </Box>
  );
}

import { Clear, RadioButtonUnchecked } from '@mui/icons-material';

function Square({ value, onClick, disabled, gameOver }) {
  const IconComponent = value === 'X' ? Clear : value === 'O' ? RadioButtonUnchecked : null;
  const iconColor = value === 'X' ? 'red' : value === 'O' ? 'blue' : 'inherit';

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
        cursor: value || disabled || gameOver ? 'default' : 'pointer',
      }}
      onClick={onClick}
    >
      {IconComponent && <IconComponent sx={{ fontSize: '48px', color: iconColor }} />}
    </Box>
  );
}
