import { test, expect } from '@playwright/test';
import { testURL, testUser, fillForm, sleep } from './utils';

test('Get correct game info', async ({ page }) => {
    await page.goto(testURL);

    let gameName = "TicTacToe";
    await page.getByRole('cell', { name: gameName }).click();

    await sleep(500);

    await expect(page.url()).toBe(testURL + '/game/' + gameName);
});

test('Start game from home logged in', async ({ page }) => {
    await page.goto(testURL + '/login');
    await fillForm(page, testUser.username, testUser.password);

    await sleep(500);

    let gameName = "TicTacToe";
    await page.getByRole('row', { name: 'TicTacToe' }).getByRole('cell').nth(1).click();

    await sleep(500);

    await expect(page.url()).toBe(testURL + '/game/' + gameName + "/findMatch");
});

test('Start game from home not logged in', async ({ page }) => {
    await page.goto(testURL);

    let gameName = "TicTacToe";
    await page.getByRole('row', { name: 'TicTacToe' }).getByRole('cell').nth(1).click();

    await sleep(500);

    await expect(page.url()).toBe(testURL + '/login');
});

test('Start game from game info logged in', async ({ page }) => {
    await page.goto(testURL + '/login');
    await fillForm(page, testUser.username, testUser.password);

    await sleep(500);

    let gameName = "TicTacToe";
    await page.getByRole('cell', { name: gameName }).click();

    await sleep(500);

    await page.getByRole('button', { name: 'Start a Match'}).click();

    await sleep(500);

    await expect(page.url()).toBe(testURL + '/game/' + gameName + "/findMatch");
});