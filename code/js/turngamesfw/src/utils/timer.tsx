import { useState, useEffect } from 'react';

export const useTimer = (targetDate) => {
  const countDownDate = new Date(targetDate).getTime();

  const [countdown, setCountDown] = useState(
    countDownDate - new Date().getTime()
  );

  useEffect(() => {
    const interval = setInterval(() => {
      setCountDown(countDownDate - new Date().getTime());
    }, 1000);

    return () => clearInterval(interval);
  }, [countDownDate]);

  return getReturnValues(countdown);
};

const getReturnValues = (countdown) => {
  const minutes = Math.floor((countdown % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((countdown % (1000 * 60)) / 1000);

  return { min: minutes, sec: seconds };
};
