package com.stellarjets.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;

import java.util.List;

/**
 * Page Object — /register
 */
public class RegisterPage extends BasePage {

    public RegisterPage(WebDriver driver, String base) {
        super(driver, base);
    }

    @Step("Abrir página de registro")
    public RegisterPage open() {
        driver.get(base + "/register");
        $(By.tagName("form"));
        return this;
    }

    @Step("Registrar: {firstName} {lastName} / {email}")
    public void register(String firstName, String lastName, String email, String password) {
        List<WebElement> inputs = $$(By.tagName("input"));
        inputs.get(0).sendKeys(firstName);
        inputs.get(1).sendKeys(lastName);
        inputs.get(2).sendKeys(email);
        inputs.get(3).sendKeys(password);
        inputs.get(4).sendKeys(password);   // confirmar contraseña
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    public boolean isFormVisible() {
        String t = mainText();
        return t.contains("registr") || t.contains("crear") || t.contains("cuenta");
    }

    public boolean hasMinFields(int min) {
        return $$(By.tagName("input")).size() >= min;
    }

    public boolean hasSuccessScreen() {
        wait.until(d -> {
            String t = d.findElement(By.tagName("main")).getText().toLowerCase();
            return t.contains("exitosa") || t.contains("confirmaci") || t.contains("correo");
        });
        String t = mainText();
        return t.contains("exitosa") || t.contains("confirmaci") || t.contains("correo");
    }

    public boolean hasErrorMessage() {
        wait.until(d -> {
            String t = d.findElement(By.tagName("main")).getText().toLowerCase();
            return t.contains("error") || t.contains("ya") || t.contains("registrado");
        });
        String t = mainText();
        return t.contains("error") || t.contains("ya") || t.contains("registrado");
    }
}
