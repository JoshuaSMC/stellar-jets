package com.stellarjets.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;

import java.util.List;

/**
 * Page Object — /reservations/:id
 */
public class ReservationPage extends BasePage {

    public ReservationPage(WebDriver driver, String base) {
        super(driver, base);
    }

    @Step("Abrir página de reserva del vuelo #{flightId}")
    public ReservationPage open(int flightId) {
        driver.get(base + "/reservations/" + flightId);
        $(By.tagName("main"));
        return this;
    }

    public boolean isLoaded() {
        try {
            return driver.findElement(By.tagName("main")).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ── US#31: Detalle del producto ──────────────────────────────────────────

    public boolean hasFlightName() {
        return !mainText().isBlank() && mainText().length() > 10;
    }

    public boolean hasRouteInfo() {
        String t = mainText();
        // Busca código IATA (3 letras mayúsculas) o flecha de ruta
        return t.matches("(?s).*[A-Z]{3}.*[A-Z]{3}.*") || t.contains("→") || t.contains("Itinerario");
    }

    public boolean hasFlightImage() {
        return !$$(By.tagName("img")).isEmpty();
    }

    public boolean hasPriceInfo() {
        String t = mainText();
        return t.contains("$") || t.contains("precio") || t.contains("Precio");
    }

    public boolean hasUserDataSection() {
        String t = mainText();
        return t.contains("@") || t.contains("usuario") || t.contains("Usuario")
                || t.contains("nombre") || t.contains("Nombre")
                || !$$(By.cssSelector("[class*='avatar'], [class*='initials']")).isEmpty();
    }

    public boolean hasSubmitButton() {
        return !$$(By.xpath("//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'confirmar')]")).isEmpty()
                || !$$(By.xpath("//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'reserva')]")).isEmpty();
    }

    // ── US#30/#32: Calendario y selección de fechas ──────────────────────────

    public boolean hasCalendar() {
        return !$$(By.cssSelector("[class*='rdp'], [class*='DayPicker'], [class*='day-picker']")).isEmpty()
                || mainText().contains("enero") || mainText().contains("febrero")
                || mainText().contains("mayo") || mainText().contains("junio")
                || mainText().contains("julio") || mainText().contains("agosto")
                || mainText().contains("septiembre") || mainText().contains("Fechas de viaje");
    }

    public boolean hasDateRangeDisplay() {
        String t = mainText();
        return t.contains("→") && (t.contains("2026") || t.contains("2027") || t.contains("Fechas"));
    }

    // ── US#32: Confirmación ──────────────────────────────────────────────────

    public boolean hasConfirmationScreen() {
        String t = mainText().toLowerCase();
        return t.contains("confirmada") || t.contains("confirmado") || t.contains("éxito")
                || t.contains("exitosa") || t.contains("procesada");
    }

    public boolean hasErrorMessage() {
        String t = mainText().toLowerCase();
        return t.contains("error") || t.contains("solapan") || t.contains("inválid")
                || t.contains("disponible") || !$$(By.cssSelector("[class*='red'], [class*='error']")).isEmpty();
    }

}
