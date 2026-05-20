package com.stellarjets.ui.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.List;

/**
 * Base Page Object: centraliza WebDriver, esperas y screenshots para Allure.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final String base;

    protected BasePage(WebDriver driver, String base) {
        this.driver = driver;
        this.base   = base;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Espera y retorna el primer elemento que coincida con el locator. */
    protected WebElement $(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /** Retorna todos los elementos que coincidan (sin espera mínima). */
    protected List<WebElement> $$(By locator) {
        return driver.findElements(locator);
    }

    /** Espera hasta que el texto del elemento cambie (útil post-clic). */
    protected void waitForText(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /** Adjunta captura de pantalla al reporte Allure. */
    protected void screenshot(String nombre) {
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(nombre, "image/png", new ByteArrayInputStream(bytes), ".png");
    }

    /** Retorna el texto del <main> en minúsculas para assertions. */
    protected String mainText() {
        return $(By.tagName("main")).getText().toLowerCase();
    }

    /** Retorna el texto del <header>. */
    public String headerText() {
        return $(By.tagName("header")).getText();
    }
}
