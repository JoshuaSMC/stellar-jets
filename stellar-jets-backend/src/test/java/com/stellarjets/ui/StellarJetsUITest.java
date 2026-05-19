package com.stellarjets.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests de UI con Selenium WebDriver — Sprint 1
 * Requiere: frontend en http://localhost:5173 y backend en http://localhost:8080
 *
 * Ejecutar con ambos servidores activos:
 *   cd stellar-jets-backend  && JAVA_HOME=... mvn spring-boot:run
 *   cd stellar-jets-frontend && npm run dev
 *   cd stellar-jets-backend  && JAVA_HOME=... mvn test -Dgroups=ui
 */
@Tag("ui")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Sprint 1 — Tests de UI (Selenium)")
class StellarJetsUITest {

    static WebDriver driver;
    static WebDriverWait wait;
    static final String BASE = "http://localhost:5173";

    @BeforeAll
    static void setup() {
        assumeTrue(servidorDisponible(BASE), "Frontend no disponible en " + BASE + " — omitiendo tests de UI");

        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage",
                "--window-size=1280,900", "--disable-gpu");
        driver = new ChromeDriver(opts);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void teardown() {
        if (driver != null) driver.quit();
    }

    // ─── US#1: Header ────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("TC-UI-01 | US#1 — Header visible en la página principal")
    void header_visible() {
        driver.get(BASE);
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("header")));
        assertTrue(header.isDisplayed());
    }

    @Test @Order(2)
    @DisplayName("TC-UI-02 | US#1 — Logo STELLAR JETS presente en el header")
    void header_logo() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("header")));
        String headerText = driver.findElement(By.tagName("header")).getText().toUpperCase();
        assertTrue(headerText.contains("STELLAR JETS"), "Logo no encontrado en el header");
    }

    @Test @Order(3)
    @DisplayName("TC-UI-03 | US#1 — Botones 'Iniciar sesión' y 'Crear cuenta' presentes")
    void header_botones_auth() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("header")));
        String texto = driver.findElement(By.tagName("header")).getText();
        assertTrue(texto.contains("Iniciar sesión"), "Falta botón 'Iniciar sesión'");
        assertTrue(texto.contains("Crear cuenta"), "Falta botón 'Crear cuenta'");
    }

    @Test @Order(4)
    @DisplayName("TC-UI-04 | US#1 — Clic en logo redirige al home")
    void header_logo_redirige_home() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("header")));
        driver.findElements(By.tagName("a")).stream()
                .filter(a -> a.getText().toUpperCase().contains("STELLAR JETS"))
                .findFirst()
                .ifPresent(WebElement::click);
        wait.until(ExpectedConditions.urlToBe(BASE + "/"));
        assertEquals(BASE + "/", driver.getCurrentUrl());
    }

    @Test @Order(5)
    @DisplayName("TC-UI-05 | US#1 — Header fijo al hacer scroll")
    void header_fijo_scroll() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("header")));
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 600)");
        WebElement header = driver.findElement(By.tagName("header"));
        String position = header.getCssValue("position");
        assertTrue(position.equals("fixed") || position.equals("sticky"),
                "Header no es fixed/sticky: " + position);
    }

    // ─── US#2: Cuerpo del sitio ───────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("TC-UI-06 | US#2 — Sección buscador visible en el home")
    void home_seccion_buscador() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        boolean hayInput = !driver.findElements(By.cssSelector("input[type='text'], input[type='search']")).isEmpty();
        assertTrue(hayInput, "No se encontró buscador en el home");
    }

    @Test @Order(7)
    @DisplayName("TC-UI-07 | US#2 — Sección recomendados visible en el home")
    void home_seccion_recomendados() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        String texto = driver.findElement(By.tagName("main")).getText().toLowerCase();
        assertTrue(texto.contains("recomend"), "No se encontró sección de recomendados");
    }

    // ─── US#4: Visualizar productos ──────────────────────────────────────────

    @Test @Order(8)
    @DisplayName("TC-UI-08 | US#4 — Se muestran productos en el home")
    void home_productos_visibles() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/flights/']")));
        List<WebElement> cards = driver.findElements(By.cssSelector("a[href*='/flights/']"));
        assertFalse(cards.isEmpty(), "No se encontraron tarjetas de vuelos");
        assertTrue(cards.size() <= 10, "Se muestran más de 10 productos: " + cards.size());
    }

    // ─── US#5: Detalle de producto ────────────────────────────────────────────

    @Test @Order(9)
    @DisplayName("TC-UI-09 | US#5 — Detalle de producto carga correctamente")
    void detalle_carga() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        assertTrue(driver.findElement(By.tagName("main")).isDisplayed());
    }

    @Test @Order(10)
    @DisplayName("TC-UI-10 | US#5 — Header hero ocupa el ancho completo")
    void detalle_header_full_width() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        WebElement hero = driver.findElement(By.cssSelector("main > div:first-child"));
        long heroWidth = (long) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].getBoundingClientRect().width;", hero);
        long viewportWidth = (long) ((JavascriptExecutor) driver)
                .executeScript("return window.innerWidth;");
        assertTrue(heroWidth >= viewportWidth - 20,
                "Hero no ocupa el ancho completo: " + heroWidth + " vs " + viewportWidth);
    }

    @Test @Order(11)
    @DisplayName("TC-UI-11 | US#5 — Botón volver presente en el detalle")
    void detalle_boton_volver() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        String texto = driver.findElement(By.tagName("main")).getText();
        assertTrue(texto.contains("Volver"), "No se encontró botón 'Volver'");
    }

    // ─── US#6: Galería ────────────────────────────────────────────────────────

    @Test @Order(12)
    @DisplayName("TC-UI-12 | US#6 — Galería de imágenes visible en el detalle")
    void galeria_visible() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("img")));
        List<WebElement> imgs = driver.findElements(By.tagName("img"));
        assertFalse(imgs.isEmpty(), "No se encontraron imágenes en el detalle");
    }

    @Test @Order(13)
    @DisplayName("TC-UI-13 | US#6 — Botón 'Ver más' presente en la galería")
    void galeria_boton_ver_mas() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        String texto = driver.findElement(By.tagName("main")).getText();
        assertTrue(texto.contains("Ver más"), "No se encontró botón 'Ver más'");
    }

    @Test @Order(14)
    @DisplayName("TC-UI-14 | US#6 — Clic en 'Ver más' abre el lightbox")
    void galeria_lightbox_abre() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        List<WebElement> botones = driver.findElements(By.xpath("//*[contains(text(),'Ver más')]"));
        assumeTrue(!botones.isEmpty(), "Botón 'Ver más' no encontrado — vuelo sin suficientes imágenes");
        botones.get(0).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[class*='fixed'][class*='inset'], [class*='lightbox'], [class*='z-50']")));
        assertTrue(driver.findElements(
                By.cssSelector("[class*='fixed'][class*='inset'], [class*='z-50']")).size() > 0,
                "Lightbox no se abrió");
    }

    // ─── US#7: Footer ─────────────────────────────────────────────────────────

    @Test @Order(15)
    @DisplayName("TC-UI-15 | US#7 — Footer visible en el home")
    void footer_visible() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("footer")));
        assertTrue(driver.findElement(By.tagName("footer")).isDisplayed());
    }

    @Test @Order(16)
    @DisplayName("TC-UI-16 | US#7 — Footer contiene logo, año y copyright")
    void footer_logo_copyright() {
        driver.get(BASE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("footer")));
        String texto = driver.findElement(By.tagName("footer")).getText().toUpperCase();
        assertTrue(texto.contains("STELLAR JETS"), "Logo no encontrado en footer");
        assertTrue(texto.contains("©") || texto.contains("COPYRIGHT") || texto.contains("2026"),
                "Copyright/año no encontrado en footer");
    }

    @Test @Order(17)
    @DisplayName("TC-UI-17 | US#7 — Footer visible en la página de detalle")
    void footer_en_detalle() {
        driver.get(BASE + "/flights/1");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("footer")));
        assertTrue(driver.findElement(By.tagName("footer")).isDisplayed());
    }

    // ─── US#9: Panel de administración ───────────────────────────────────────

    @Test @Order(18)
    @DisplayName("TC-UI-18 | US#9 — Panel admin accesible en /administracion")
    void admin_url_accesible() {
        driver.get(BASE + "/administracion");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        String texto = driver.findElement(By.tagName("main")).getText().toLowerCase();
        assertTrue(texto.contains("administr") || texto.contains("panel"),
                "Panel de administración no encontrado");
    }

    @Test @Order(19)
    @DisplayName("TC-UI-19 | US#9 — Panel admin contiene botón 'Lista de productos'")
    void admin_boton_lista() {
        driver.get(BASE + "/administracion");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        String texto = driver.findElement(By.tagName("main")).getText();
        assertTrue(texto.contains("Lista de productos"), "Botón 'Lista de productos' no encontrado");
    }

    @Test @Order(20)
    @DisplayName("TC-UI-20 | US#9 — Panel admin contiene botón 'Agregar producto'")
    void admin_boton_agregar() {
        driver.get(BASE + "/administracion");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        String texto = driver.findElement(By.tagName("main")).getText();
        assertTrue(texto.contains("Agregar producto"), "Botón 'Agregar producto' no encontrado");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static boolean servidorDisponible(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(3000);
            c.setRequestMethod("HEAD");
            return c.getResponseCode() < 500;
        } catch (Exception e) {
            return false;
        }
    }
}
