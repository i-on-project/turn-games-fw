import { test, expect } from '@playwright/test';
import { testURL, testUser, fillForm, sleep } from './utils';



test('Valid Register', async ({ page }) => {
    await page.goto(testURL + '/register');
  
    let testUser = "TestUser" + Math.floor(Math.random() * 1000000);
    let testPassword = "password";

    await fillForm(page, testUser, testPassword);
  
    await expect(page.getByRole('heading', { name: 'Registered with success' })).toBeVisible();
});

test('Username Already Taken', async ({ page }) => {
    await page.goto(testURL + '/register');

    await fillForm(page, testUser.username, testUser.password);

    // If the testUser was not registered, try to register it again
    if (await page.isVisible('text=Registered with success')) {
        await fillForm(page, testUser.username, testUser.password);
        await page.goto(testURL);
        await page.goto(testURL + '/register');
    }
    
    await sleep(500);
    await expect(await page.getByText('Username taken').isVisible()).toBe(true);
});
    
test('Empty Username', async ({ page }) => {
    await page.goto(testURL + '/register');

    await fillForm(page, "", testUser.password);

    await expect(await page.getByText('Username missing').isVisible()).toBe(true);
});

test('Empty Password', async ({ page }) => {
    await page.goto(testURL + '/register');
    await page.getByRole('button', { name: 'Register' }).click();

    await fillForm(page, testUser.username, "");
    
    await expect(await page.getByText('Password missing').isVisible()).toBe(true);
});

test('Go to Login', async ({ page }) => {
    await page.goto(testURL + '/register');
    
    await page.getByText('Already have an account? Sign In').click();
	
    await expect(page.url()).toBe(testURL + '/login');
});
