package com.stellarjets.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;

import java.util.List;

/**
 * Page Object — /flights/:id
 */
public class FlightDetailPage extends BasePage {

    public FlightDetailPage(WebDriver driver, String base) {
        super(driver, base);
    }

    @Step("Abrir detalle del vuelo #{id}")
    public FlightDetailPage open(int id) {
        driver.get(base + "/flights/" + id);
        $(By.tagName("main"));
        return this;
    }

    public boolean isLoaded() {
        return driver.findElement(By.tagName("main")).isDisplayed();
    }

    public boolean hasBackButton() {
        return mainText().contains("volver");
    }

    public boolean heroIsFullWidth() {
        WebElement hero = driver.findElement(By.cssSelector("main > div:first-child"));
        long heroW = (long) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].getBoundingClientRect().width;", hero);
        long viewW = (long) ((JavascriptExecutor) driver)
                .executeScript("return window.innerWidth;");
        return heroW >= viewW - 20;
    }

    // ── Galería ───────────────────────────────────────────────────────────────

    public boolean hasImages() {
        return !$$(By.tagName("img")).isEmpty();
    }

    public boolean hasVerMasButton() {
        return mainText().contains("ver más");
    }

    @Step("Clic en 'Ver más' de la galería")
    public void clickVerMas() {
        List<WebElement> btns = $$(By.xpath("//*[contains(text(),'Ver más')]"));
        if (!btns.isEmpty()) {
            WebElement btn = btns.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public boolean isLightboxOpen() {
        return !$$(By.cssSelector("[class*='fixed'][class*='inset'], [class*='z-50']")).isEmpty();
    }

    // ── Características ───────────────────────────────────────────────────────

    public boolean hasCharacteristicsSection() {
        String t = $(By.tagName("main")).getText();
        return t.contains("Características") || t.contains("característica");
    }

    public boolean hasCharacteristicText() {
        String t = mainText();
        return t.contains("wifi") || t.contains("asiento") || t.contains("gourmet")
                || t.contains("equipaje") || t.contains("entretenimiento");
    }
}
