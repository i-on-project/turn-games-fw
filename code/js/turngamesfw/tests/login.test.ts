import { test, expect } from '@playwright/test';
import { testURL, fillForm, sleep } from './utils';

test('Valid Login', async ({ page }) => {
	await page.goto(testURL + '/login');

	let testUser = "TestUser";
	let testPassword = "password";

	await fillForm(page, testUser, testPassword);

	if(await page.isVisible('text=Username taken')) {
		await page.goto(testURL + '/register');
		await fillForm(page, testUser, testPassword);
	}

	await sleep(1000);
	await expect(page.url()).toBe(testURL + '/');
});

test('Invalid Credentials', async ({ page }) => {
	await page.goto(testURL + '/login');

    let testUser = "TestUser";
    let testPassword = "wrong password";

    await fillForm(page, testUser, testPassword);

	await sleep(1000);

    await expect(await page.getByText('Invalid username or password').isVisible()).toBe(true);
});
    
test('Empty Username', async ({ page }) => {
	await page.goto(testURL + '/login');

    await fillForm(page, "", "password");

    await expect(await page.getByText('Username missing').isVisible()).toBe(true);
});

test('Empty Password', async ({ page }) => {
	await page.goto(testURL + '/login');

	await fillForm(page, "TestUser", "");
	
	await expect(await page.getByText('Password missing').isVisible()).toBe(true);
});

test('Go to Register', async ({ page }) => {
	await page.goto(testURL + '/login');

	await page.getByText('Don\'t have an account? Sign Up').click();

	await expect(page.url()).toBe(testURL + '/register');
});