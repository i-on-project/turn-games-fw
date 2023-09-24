import { test, expect } from '@playwright/test';
import { fillForm, sleep } from './utils';

test('Valid Register', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await page.getByRole('button', { name: 'Register' }).click();
  
    let testUser = "TestUser" + Math.floor(Math.random() * 1000000);
    let testPassword = "password";

    await fillForm(page, testUser, testPassword);
  
    await expect(page.getByRole('heading', { name: 'Registered with success' })).toBeVisible();
});

test('Username Already Taken', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await page.getByRole('button', { name: 'Register' }).click();

    let testUser = "TestUser";
    let testPassword = "password";

    await fillForm(page, testUser, testPassword);

    // If the testUser was not registered, try to register it again
    if (await page.isVisible('text=Registered with success')) {
        await fillForm(page, testUser, testPassword);
        await page.getByRole('button', { name: 'Register' }).click();
    }
    
    await sleep(1000);
    await expect(await page.getByText('Username taken').isVisible()).toBe(true);
});
    
test('Empty Username', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await page.getByRole('button', { name: 'Register' }).click();

    await fillForm(page, "", "password");

    await expect(await page.getByText('Username missing').isVisible()).toBe(true);
});

test('Empty Password', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    await page.getByRole('button', { name: 'Register' }).click();

    await fillForm(page, "TestUser", "");
    
    await expect(await page.getByText('Password missing').isVisible()).toBe(true);
});
