import { test, expect } from '@playwright/test';
import { testURL, testUser, fillForm, sleep } from './utils';

test('Valid Login', async ({ page }) => {
	await page.goto(testURL + '/login');

	await fillForm(page, testUser.username, testUser.password);

	await sleep(500);
	if(await page.getByText('Invalid username or password').isVisible()) {
		await page.goto(testURL + '/register');
		await fillForm(page, testUser.username, testUser.password);

		await page.getByText('Login here').click();
		await fillForm(page, testUser.username, testUser.password);
	}

	await sleep(500);
	await expect(page.url()).toBe(testURL + '/');
});

test('Invalid Credentials', async ({ page }) => {
	await page.goto(testURL + '/login');

    await fillForm(page, testUser.username, "wrongPassword");

	await sleep(500);

    await expect(await page.getByText('Invalid username or password').isVisible()).toBe(true);
});
    
test('Empty Username', async ({ page }) => {
	await page.goto(testURL + '/login');

    await fillForm(page, "", testUser.password);

    await expect(await page.getByText('Username missing').isVisible()).toBe(true);
});

test('Empty Password', async ({ page }) => {
	await page.goto(testURL + '/login');

	await fillForm(page, testUser.username, "");
	
	await expect(await page.getByText('Password missing').isVisible()).toBe(true);
});

test('Go to Register', async ({ page }) => {
	await page.goto(testURL + '/login');

	await page.getByText('Don\'t have an account? Sign Up').click();

	await expect(page.url()).toBe(testURL + '/register');
});