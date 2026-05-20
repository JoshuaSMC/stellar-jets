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
