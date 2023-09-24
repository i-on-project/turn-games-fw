export const testURL = 'http://localhost:8000';

export async function fillForm(page, username, password) {
    await page.getByLabel('Username *').click();
    await page.getByLabel('Username *').fill(username);
    await page.getByLabel('Password *').click();
    await page.getByLabel('Password *').fill(password);
    await page.getByRole('button', { name: 'Submit' }).click();
}
  
export async function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}