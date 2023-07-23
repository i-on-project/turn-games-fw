import { test, expect } from '@playwright/test';

test('testLogin', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Login' }).click();
  await page.getByLabel('Username *').click();
  await page.getByLabel('Username *').fill('User1');
  await page.getByLabel('Password *').click();
  await page.getByLabel('Password *').fill('password');
  await page.getByLabel('Password *').press('Enter');
  await page.getByText('Invalid username or password').click();
  await expect(page.getByText('Invalid username or password')).toBeVisible();
});

test('testRegisterThenLogin', async ({ page }) => {
    await page.goto('http://localhost:8000/');
    
    await page.getByRole('button', { name: 'Register' }).click();
    await page.getByLabel('Username *').click();
    await page.getByLabel('Username *').fill('User1');
    await page.getByLabel('Password *').click();
    await page.getByLabel('Password *').fill('password');
    await page.getByRole('button', { name: 'Submit' }).click();
    await expect(page.getByRole('heading', { name: 'Registered with success' })).toBeVisible();
    
    await page.getByText('Login here').click();
    await page.getByLabel('Username *').fill('User1');
    await page.getByLabel('Password *').click();
    await page.getByLabel('Password *').fill('password');
    await page.getByLabel('Password *').press('Enter');
    await expect(page.getByRole('button', { name: 'User1' })).toBeVisible();
  });