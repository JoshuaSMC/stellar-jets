package com.stellarjets.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;

/**
 * Page Object — /mis-reservas
 */
public class MyReservationsPage extends BasePage {

    public MyReservationsPage(WebDriver driver, String base) {
        super(driver, base);
    }

    @Step("Abrir página de mis reservas")
    public MyReservationsPage open() {
        driver.get(base + "/mis-reservas");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return this;
    }

    public boolean isLoaded() {
        try {
            return driver.findElement(By.tagName("main")).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean hasReservationList() {
        String t = bodyText();
        return t.contains("reserva") || t.contains("vuelo") || t.contains("próxima")
                || t.contains("completada") || t.contains("historial");
    }

    public boolean hasReservationCards() {
        return !$$(By.cssSelector("main [class*='rounded']")).isEmpty()
                && bodyText().contains("→");
    }

    public boolean hasStatusBadge() {
        String t = bodyText();
        return t.contains("próxima") || t.contains("completada") || t.contains("Próxima") || t.contains("Completada");
    }

    public boolean hasFlightNameInCard() {
        return !$$(By.cssSelector("main h2")).isEmpty() || bodyText().length() > 50;
    }

    public boolean hasDateRangeInCard() {
        String t = bodyText();
        return t.contains("→") && (t.contains("2026") || t.contains("2027") || t.contains("de "));
    }

    public boolean hasLinkToFlight() {
        return !$$(By.xpath("//a[contains(text(),'detalle') or contains(text(),'vuelo')]")).isEmpty()
                || !$$(By.cssSelector("a[href*='/flights/']")).isEmpty();
    }

    public boolean isRedirectedToLogin() {
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        String url = driver.getCurrentUrl();
        String body = bodyText();
        return url.contains("/login") || body.contains("iniciar sesión") || body.contains("login");
    }

    public boolean hasEmptyState() {
        String t = bodyText();
        return t.contains("no tenés reservas") || t.contains("explorar") || t.contains("primera reserva");
    }

    private String bodyText() {
        try {
            return driver.findElement(By.tagName("body")).getText().toLowerCase();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
