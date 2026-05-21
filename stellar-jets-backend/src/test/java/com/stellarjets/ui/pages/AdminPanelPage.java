package com.stellarjets.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object — /administracion
 */
public class AdminPanelPage extends BasePage {

    public AdminPanelPage(WebDriver driver, String base) {
        super(driver, base);
    }

    @Step("Abrir panel de administración")
    public AdminPanelPage open() {
        driver.get(base + "/administracion");
        $(By.tagName("main"));
        return this;
    }

    public boolean isPanelVisible() {
        String t = mainText();
        return t.contains("administr") || t.contains("panel");
    }

    public boolean hasTabs() {
        String t = $(By.tagName("main")).getText();
        return t.contains("Características") || t.contains("Categorías") || t.contains("Lista de productos");
    }

    public boolean hasProductListTab() {
        return $(By.tagName("main")).getText().contains("Lista de productos");
    }

    public boolean hasAddProductButton() {
        return $(By.tagName("main")).getText().contains("Agregar producto");
    }

    // ── Características ───────────────────────────────────────────────────────

    public boolean hasCharacteristicsSection() {
        String t = $(By.tagName("main")).getText();
        return t.contains("Características") || t.contains("característica");
    }

    @Step("Navegar al tab de Características")
    public void goToCharacteristics() {
        List<WebElement> tab = $$(By.xpath(
            "//*[contains(text(),'Característica') or contains(text(),'característica')]"));
        if (!tab.isEmpty()) tab.get(0).click();
        wait.until(d -> {
            String t = d.findElement(By.tagName("main")).getText().toLowerCase();
            return t.contains("wifi") || t.contains("asiento") || t.contains("gourmet");
        });
    }

    public boolean hasCharacteristicData() {
        String t = mainText();
        return t.contains("wifi") || t.contains("asiento") || t.contains("gourmet");
    }

    // ── Categorías ────────────────────────────────────────────────────────────

    public boolean hasCategoriesSection() {
        String t = $(By.tagName("main")).getText();
        return t.contains("Categoría") || t.contains("categoría");
    }

    @Step("Navegar al tab de Categorías")
    public void goToCategories() {
        List<WebElement> tab = $$(By.xpath(
            "//*[contains(text(),'Categoría') or contains(text(),'categoría')]"));
        if (!tab.isEmpty()) tab.get(0).click();
        wait.until(d -> {
            String t = d.findElement(By.tagName("main")).getText().toLowerCase();
            return t.contains("lujo") || t.contains("aventura") || t.contains("negocios");
        });
    }

    public boolean hasCategoryData() {
        String t = mainText();
        return t.contains("lujo") || t.contains("aventura") || t.contains("negocios");
    }

    // ── Eliminar categoría (US#29) ────────────────────────────────────────────

    // El botón de eliminar categoría es un ícono Trash2 con title="Eliminar" (sin texto visible)
    public boolean hasDeleteButtonInCategories() {
        goToCategories();
        return !$$(By.cssSelector("button[title='Eliminar']")).isEmpty();
    }

    @Step("Clic en botón Eliminar de la primera categoría")
    public void clickDeleteFirstCategory() {
        goToCategories();
        List<WebElement> btns = $$(By.cssSelector("button[title='Eliminar']"));
        if (!btns.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btns.get(0));
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btns.get(0));
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        }
    }

    // El modal React muestra el texto "Eliminar categor" y "Confirmar eliminación"
    public boolean hasConfirmationModal() {
        String t = driver.findElement(By.tagName("body")).getText();
        return t.contains("Eliminar categor") || t.contains("Confirmar eliminaci");
    }

    public boolean modalShowsCategoryName() {
        String t = driver.findElement(By.tagName("body")).getText();
        return t.contains("Lujo") || t.contains("Aventura") || t.contains("Negocios") || t.contains("Familia");
    }

    @Step("Clic en Cancelar en el modal de confirmación")
    public void clickCancelInModal() {
        // JS click: evita ElementClickIntercepted si el overlay cubre el botón
        List<WebElement> cancel = $$(By.xpath("//button[text()='Cancelar']"));
        if (!cancel.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cancel.get(0));
        }
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    @Step("Clic en Confirmar eliminación en el modal")
    public void clickConfirmInModal() {
        List<WebElement> confirm = $$(By.xpath("//button[contains(text(),'Confirmar eliminaci')]"));
        if (!confirm.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirm.get(0));
        }
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
    }

    public int countCategoryRows() {
        goToCategories();
        return $$(By.cssSelector("table tbody tr, [data-testid='category-row']")).size();
    }

    // ── Favoritos (US#25) ─────────────────────────────────────────────────────

    public boolean hasFavoritesSection() {
        String t = mainText();
        return t.contains("favorito") || t.contains("mis vuelos");
    }

    // ── Avatar / Logout ───────────────────────────────────────────────────────

    @Step("Abrir dropdown del avatar para logout")
    public void openAvatarDropdown() {
        List<WebElement> avatar = $$(By.xpath(
            "//header//button[.//div[contains(@class,'rounded-full')]]"));
        if (!avatar.isEmpty()) avatar.get(0).click();
    }

    @Step("Cerrar sesión")
    public void logout() {
        openAvatarDropdown();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'Cerrar sesión') or contains(text(),'Salir')]")));
        driver.findElement(
            By.xpath("//*[contains(text(),'Cerrar sesión') or contains(text(),'Salir')]")).click();
    }

    public boolean isLoggedOut() {
        wait.until(d -> {
            String h = d.findElement(By.tagName("header")).getText();
            return h.contains("Iniciar sesión") || h.contains("Crear cuenta");
        });
        String h = headerText();
        return h.contains("Iniciar sesión") || h.contains("Crear cuenta");
    }
}
