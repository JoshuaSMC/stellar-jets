package com.stellarjets.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object — /login
 */
public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver, String base) {
        super(driver, base);
    }

    @Step("Abrir página de login")
    public LoginPage open() {
        driver.get(base + "/login");
        $(By.tagName("form"));
        return this;
    }

    @Step("Iniciar sesión como '{email}'")
    public void loginAs(String email, String password) {
        open();
        fillCredentials(email, password);
        submit();
        wait.until(ExpectedConditions.urlToBe(base + "/"));
    }

    @Step("Intentar login con credenciales incorrectas")
    public void loginWithBadCredentials(String email, String password) {
        open();
        fillCredentials(email, password);
        submit();
    }

    public boolean isFormVisible() {
        return $$(By.tagName("input")).size() >= 2;
    }

    public boolean hasErrorMessage() {
        wait.until(d -> {
            String t = d.findElement(By.tagName("main")).getText().toLowerCase();
            return t.contains("error") || t.contains("incorrecta") || t.contains("inválid");
        });
        String t = mainText();
        return t.contains("error") || t.contains("incorrecta") || t.contains("inválid");
    }

    @Step("Limpiar sesión (localStorage)")
    public void clearSession() {
        driver.get(base);
        ((JavascriptExecutor) driver).executeScript("localStorage.clear()");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void fillCredentials(String email, String password) {
        List<WebElement> inputs = $$(By.tagName("input"));
        inputs.get(0).sendKeys(email);
        inputs.get(1).sendKeys(password);
    }

    private void submit() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
}
