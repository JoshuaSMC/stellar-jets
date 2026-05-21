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

    // ── Disponibilidad (US#23) ────────────────────────────────────────────────

    public boolean hasAvailabilityCalendar() {
        String t = mainText();
        return t.contains("disponib") || t.contains("calendar") || t.contains("fecha")
                || !$$(By.cssSelector("[class*='rdp'], [class*='calendar'], [class*='Calendar']")).isEmpty();
    }

    public boolean hasCalendarMonthVisible() {
        return !$$(By.cssSelector(
            "[class*='rdp-month'], [class*='month'], [aria-label*='calendar']")).isEmpty()
            || mainText().contains("enero") || mainText().contains("febrero")
            || mainText().contains("mayo") || mainText().contains("junio")
            || mainText().contains("julio") || mainText().contains("agosto");
    }

    // ── Favorito (US#24) ──────────────────────────────────────────────────────

    public boolean hasFavoriteButton() {
        return !$$(By.cssSelector("[data-testid='favorite-btn'], button[aria-label*='favorito']")).isEmpty()
            || !$$(By.cssSelector("button svg[class*='heart'], button[class*='heart']")).isEmpty()
            || mainText().contains("favorito");
    }

    // ── Políticas (US#26) ─────────────────────────────────────────────────────

    public boolean hasPoliciesSection() {
        String t = mainText();
        return t.contains("política") || t.contains("políticas") || t.contains("condiciones")
                || t.contains("cancelación") || t.contains("normas");
    }

    public boolean hasPoliciesTitle() {
        return !$$(By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNÑOPQRSTUVWXYZ','abcdefghijklmnñopqrstuvwxyz'),'política') "
                + "or contains(translate(text(),'ABCDEFGHIJKLMNÑOPQRSTUVWXYZ','abcdefghijklmnñopqrstuvwxyz'),'normas')]")).isEmpty();
    }

    // ── Compartir (US#27) ─────────────────────────────────────────────────────

    public boolean hasShareButton() {
        String t = mainText();
        return t.contains("compartir") || t.contains("compartí")
            || !$$(By.cssSelector("button[aria-label*='compartir'], [data-testid='share-btn']")).isEmpty()
            || !$$(By.xpath("//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'compartir')]")).isEmpty();
    }

    @Step("Clic en botón Compartir")
    public void clickShare() {
        List<WebElement> btns = $$(By.xpath(
            "//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'compartir')]"));
        if (!btns.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btns.get(0));
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btns.get(0));
        }
    }

    public boolean hasShareModalOrMenu() {
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        String t = $(By.tagName("body")).getText().toLowerCase();
        return t.contains("facebook") || t.contains("twitter") || t.contains("instagram")
            || t.contains("whatsapp") || t.contains("compartir en")
            || !$$(By.cssSelector("[class*='modal'], [class*='dialog'], [role='dialog']")).isEmpty();
    }

    // ── Reseñas (US#28) ───────────────────────────────────────────────────────

    public boolean hasReviewsSection() {
        String t = mainText();
        return t.contains("reseña") || t.contains("opinión") || t.contains("puntua")
                || t.contains("calificaci");
    }

    public boolean hasRatingInput() {
        return !$$(By.cssSelector("input[type='radio'][name*='star'], [class*='star'][role='button'], [aria-label*='estrella']")).isEmpty()
            || mainText().contains("estrella");
    }

    public boolean hasReviewForm() {
        return !$$(By.cssSelector("textarea, input[type='text'][placeholder*='comentario']")).isEmpty();
    }
}
