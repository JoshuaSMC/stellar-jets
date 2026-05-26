package com.stellarjets.ui;

import com.stellarjets.ui.pages.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests de UI — Sprint 1 · Page Object Model + Allure Reports
 *
 * Requiere: frontend en http://localhost:5173 y backend en http://localhost:8080
 */
@Epic("Stellar Jets — Frontend")
@Tag("ui")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Sprint 1 — Tests de UI (Selenium)")
class Sprint1UITest {

    static WebDriver driver;
    static final String BASE        = "http://localhost:5173";
    static final String ADMIN_EMAIL = "admin@stellarjets.com";
    static final String ADMIN_PASS  = "admin123";

    // Page Objects
    static HomePage         homePage;
    static LoginPage        loginPage;
    static FlightDetailPage detailPage;
    static AdminPanelPage   adminPage;

    @BeforeAll
    static void setup() {
        assumeTrue(servidorDisponible(BASE),
                "Frontend no disponible en " + BASE + " — omitiendo tests de UI Sprint 1");

        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage",
                "--window-size=1280,900", "--disable-gpu");
        driver = new ChromeDriver(opts);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));

        homePage   = new HomePage(driver, BASE);
        loginPage  = new LoginPage(driver, BASE);
        detailPage = new FlightDetailPage(driver, BASE);
        adminPage  = new AdminPanelPage(driver, BASE);
    }

    @AfterAll
    static void teardown() {
        if (driver != null) driver.quit();
    }

    @BeforeEach
    void limpiarSesion() {
        loginPage.clearSession();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#1 — Header
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(1)
    @Feature("US#1 — Header")
    @Story("Header visible en home")
    @DisplayName("TC-UI-01 | US#1 — Header visible en la página principal")
    void header_visible() {
        homePage.open();
        assertTrue(homePage.isHeaderVisible());
    }

    @Test @Order(2)
    @Feature("US#1 — Header")
    @Story("Logo STELLAR JETS en header")
    @DisplayName("TC-UI-02 | US#1 — Logo STELLAR JETS presente en el header")
    void header_logo() {
        homePage.open();
        assertTrue(homePage.hasLogo(), "Logo no encontrado en el header");
    }

    @Test @Order(3)
    @Feature("US#1 — Header")
    @Story("Botones de autenticación en header")
    @DisplayName("TC-UI-03 | US#1 — Botones 'Iniciar sesión' y 'Crear cuenta' presentes")
    void header_botones_auth() {
        homePage.open();
        assertTrue(homePage.hasAuthButtons(), "Botones de auth no encontrados");
    }

    @Test @Order(4)
    @Feature("US#1 — Header")
    @Story("Logo redirige al home")
    @DisplayName("TC-UI-04 | US#1 — Clic en logo redirige al home")
    void header_logo_redirige_home() {
        detailPage.open(1);
        driver.findElements(By.tagName("a")).stream()
                .filter(a -> a.getText().toUpperCase().contains("STELLAR JETS"))
                .findFirst()
                .ifPresent(WebElement::click);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getCurrentUrl().equals(BASE + "/"));
        assertEquals(BASE + "/", driver.getCurrentUrl());
    }

    @Test @Order(5)
    @Feature("US#1 — Header")
    @Story("Header fijo al hacer scroll")
    @DisplayName("TC-UI-05 | US#1 — Header fijo al hacer scroll")
    void header_fijo_scroll() {
        homePage.open();
        homePage.scrollDown(600);
        assertTrue(homePage.isHeaderFixed(), "Header no es fixed/sticky");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#2 — Cuerpo del sitio
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(6)
    @Feature("US#2 — Home")
    @Story("Buscador visible en home")
    @DisplayName("TC-UI-06 | US#2 — Sección buscador visible en el home")
    void home_seccion_buscador() {
        homePage.open();
        assertTrue(homePage.hasSearchBar(), "No se encontró buscador en el home");
    }

    @Test @Order(7)
    @Feature("US#2 — Home")
    @Story("Sección recomendados visible")
    @DisplayName("TC-UI-07 | US#2 — Sección recomendados visible en el home")
    void home_seccion_recomendados() {
        homePage.open();
        assertTrue(homePage.hasRecommendedSection(), "No se encontró sección de recomendados");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#4 — Visualizar productos
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(8)
    @Feature("US#4 — Listado de vuelos")
    @Story("Tarjetas de vuelos visibles en home")
    @DisplayName("TC-UI-08 | US#4 — Se muestran productos en el home")
    void home_productos_visibles() {
        homePage.open();
        // El home tiene sección recomendados (desktop+mobile) y grilla paginada
        assertFalse(homePage.getFlightCards().isEmpty(), "No se encontraron tarjetas de vuelos");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#5 — Detalle de producto
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(9)
    @Feature("US#5 — Detalle de vuelo")
    @Story("Página de detalle carga correctamente")
    @DisplayName("TC-UI-09 | US#5 — Detalle de producto carga correctamente")
    void detalle_carga() {
        detailPage.open(1);
        assertTrue(detailPage.isLoaded());
    }

    @Test @Order(10)
    @Feature("US#5 — Detalle de vuelo")
    @Story("Hero ocupa ancho completo")
    @DisplayName("TC-UI-10 | US#5 — Header hero ocupa el ancho completo")
    void detalle_header_full_width() {
        detailPage.open(1);
        assertTrue(detailPage.heroIsFullWidth(), "Hero no ocupa el ancho completo");
    }

    @Test @Order(11)
    @Feature("US#5 — Detalle de vuelo")
    @Story("Botón Volver presente")
    @DisplayName("TC-UI-11 | US#5 — Botón volver presente en el detalle")
    void detalle_boton_volver() {
        detailPage.open(1);
        assertTrue(detailPage.hasBackButton(), "No se encontró botón 'Volver'");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#6 — Galería
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(12)
    @Feature("US#6 — Galería de imágenes")
    @Story("Galería de imágenes visible en detalle")
    @DisplayName("TC-UI-12 | US#6 — Galería de imágenes visible en el detalle")
    void galeria_visible() {
        detailPage.open(1);
        assertTrue(detailPage.hasImages(), "No se encontraron imágenes en el detalle");
    }

    @Test @Order(13)
    @Feature("US#6 — Galería de imágenes")
    @Story("Botón Ver más presente")
    @DisplayName("TC-UI-13 | US#6 — Botón 'Ver más' presente en la galería")
    void galeria_boton_ver_mas() {
        detailPage.open(1);
        assertTrue(detailPage.hasVerMasButton(), "No se encontró botón 'Ver más'");
    }

    @Test @Order(14)
    @Feature("US#6 — Galería de imágenes")
    @Story("Lightbox se abre al hacer clic en Ver más")
    @DisplayName("TC-UI-14 | US#6 — Clic en 'Ver más' abre el lightbox")
    void galeria_lightbox_abre() {
        detailPage.open(1);
        assumeTrue(detailPage.hasVerMasButton(), "Botón 'Ver más' no encontrado");
        detailPage.clickVerMas();
        assertTrue(detailPage.isLightboxOpen(), "Lightbox no se abrió");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#7 — Footer
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(15)
    @Feature("US#7 — Footer")
    @Story("Footer visible en home")
    @DisplayName("TC-UI-15 | US#7 — Footer visible en el home")
    void footer_visible() {
        homePage.open();
        assertTrue(homePage.isFooterVisible());
    }

    @Test @Order(16)
    @Feature("US#7 — Footer")
    @Story("Footer contiene logo y año")
    @DisplayName("TC-UI-16 | US#7 — Footer contiene logo, año y copyright")
    void footer_logo_copyright() {
        homePage.open();
        assertTrue(homePage.footerHasLogoAndYear(), "Logo/año no encontrado en footer");
    }

    @Test @Order(17)
    @Feature("US#7 — Footer")
    @Story("Footer visible en detalle")
    @DisplayName("TC-UI-17 | US#7 — Footer visible en la página de detalle")
    void footer_en_detalle() {
        detailPage.open(1);
        assertTrue(driver.findElement(By.tagName("footer")).isDisplayed());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#9 — Panel de administración
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(18)
    @Feature("US#9 — Panel admin")
    @Story("Panel admin accesible en /administracion")
    @DisplayName("TC-UI-18 | US#9 — Panel admin accesible en /administracion")
    void admin_url_accesible() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        assertTrue(adminPage.isPanelVisible(), "Panel de administración no encontrado");
    }

    @Test @Order(19)
    @Feature("US#9 — Panel admin")
    @Story("Botón Lista de productos en panel")
    @DisplayName("TC-UI-19 | US#9 — Panel admin contiene botón 'Lista de productos'")
    void admin_boton_lista() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        assertTrue(adminPage.hasProductListTab(), "Botón 'Lista de productos' no encontrado");
    }

    @Test @Order(20)
    @Feature("US#9 — Panel admin")
    @Story("Botón Agregar producto en panel")
    @DisplayName("TC-UI-20 | US#9 — Panel admin contiene botón 'Agregar producto'")
    void admin_boton_agregar() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        assertTrue(adminPage.hasAddProductButton(), "Botón 'Agregar producto' no encontrado");
    }

    // ── helper ────────────────────────────────────────────────────────────────

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
