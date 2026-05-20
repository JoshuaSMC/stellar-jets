package com.stellarjets.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object — / (Home)
 */
public class HomePage extends BasePage {

    public HomePage(WebDriver driver, String base) {
        super(driver, base);
    }

    @Step("Abrir página principal")
    public HomePage open() {
        driver.get(base);
        $(By.tagName("main"));
        return this;
    }

    // ── Header ────────────────────────────────────────────────────────────────

    public boolean isHeaderVisible() {
        return driver.findElement(By.tagName("header")).isDisplayed();
    }

    public boolean hasLogo() {
        return headerText().toUpperCase().contains("STELLAR JETS");
    }

    public boolean hasAuthButtons() {
        String h = headerText();
        return h.contains("Iniciar sesión") && h.contains("Crear cuenta");
    }

    public boolean hasUserName(String name) {
        return headerText().contains(name);
    }

    public boolean isHeaderFixed() {
        String pos = driver.findElement(By.tagName("header")).getCssValue("position");
        return pos.equals("fixed") || pos.equals("sticky");
    }

    public void scrollDown(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + pixels + ")");
    }

    // ── Búsqueda ──────────────────────────────────────────────────────────────

    public boolean hasSearchBar() {
        return !$$(By.cssSelector("input[type='text'], input[type='search']")).isEmpty();
    }

    // ── Vuelos ────────────────────────────────────────────────────────────────

    public List<WebElement> getFlightCards() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/flights/']")));
        return $$(By.cssSelector("a[href*='/flights/']"));
    }

    public boolean hasRecommendedSection() {
        return mainText().contains("recomend");
    }

    // ── Categorías ────────────────────────────────────────────────────────────

    public boolean hasCategoryCards() {
        String t = mainText();
        return t.contains("lujo") || t.contains("aventura") || t.contains("negocios") || t.contains("familia");
    }

    public boolean hasTodosCard() {
        return $(By.tagName("main")).getText().contains("Todos");
    }

    @Step("Clic en tarjeta 'Todos'")
    public void clickTodos() {
        List<WebElement> btns = $$(By.xpath("//button[contains(text(),'Todos')]"));
        if (!btns.isEmpty()) btns.get(0).click();
    }

    public List<WebElement> getCategoryButtons(String... labels) {
        StringBuilder xp = new StringBuilder("//button[");
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) xp.append(" or ");
            xp.append("contains(text(),'").append(labels[i]).append("')");
        }
        xp.append("]");
        return $$(By.xpath(xp.toString()));
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    public boolean isFooterVisible() {
        return driver.findElement(By.tagName("footer")).isDisplayed();
    }

    public boolean footerHasLogoAndYear() {
        String t = driver.findElement(By.tagName("footer")).getText().toUpperCase();
        return t.contains("STELLAR JETS") && (t.contains("©") || t.contains("2026"));
    }
}
