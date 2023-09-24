import { test, expect } from '@playwright/test';
import { testURL, fillForm, sleep } from './utils';

test('Valid Register', async ({ page }) => {
    await page.goto(testURL + '/register');
  
    let testUser = "TestUser" + Math.floor(Math.random() * 1000000);
    let testPassword = "password";

    await fillForm(page, testUser, testPassword);
  
    await expect(page.getByRole('heading', { name: 'Registered with success' })).toBeVisible();
});

test('Username Already Taken', async ({ page }) => {
    await page.goto(testURL + '/register');

    let testUser = "TestUser";
    let testPassword = "password";

    await fillForm(page, testUser, testPassword);

    // If the testUser was not registered, try to register it again
    if (await page.isVisible('text=Registered with success')) {
        await fillForm(page, testUser, testPassword);
        await page.goto(testURL);
        await page.goto(testURL + '/register');
    }
    
    await sleep(1000);
    await expect(await page.getByText('Username taken').isVisible()).toBe(true);
});
    
test('Empty Username', async ({ page }) => {
    await page.goto(testURL + '/register');

    await fillForm(page, "", "password");

    await expect(await page.getByText('Username missing').isVisible()).toBe(true);
});

test('Empty Password', async ({ page }) => {
    await page.goto(testURL + '/register');
    await page.getByRole('button', { name: 'Register' }).click();

    await fillForm(page, "TestUser", "");
    
    await expect(await page.getByText('Password missing').isVisible()).toBe(true);
});

test('Go to Login', async ({ page }) => {
    await page.goto(testURL + '/register');
    
    await page.getByText('Already have an account? Sign In').click();
	
    await expect(page.url()).toBe(testURL + '/login');
});
