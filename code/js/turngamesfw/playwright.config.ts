import type { PlaywrightTestConfig } from '@playwright/test';

const config: PlaywrightTestConfig = {
    testMatch: [
        "tests/login.test.ts",
        "tests/register.test.ts",
    ],
};

export default config;