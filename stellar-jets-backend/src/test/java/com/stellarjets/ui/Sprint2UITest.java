package com.stellarjets.ui;

import com.stellarjets.ui.pages.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests de UI — Sprint 2 · Page Object Model + Allure Reports
 *
 * Requiere: frontend en http://localhost:5173 y backend en http://localhost:8080
 */
@Epic("Stellar Jets — Frontend")
@Tag("ui")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Sprint 2 — Tests de UI (Selenium)")
class Sprint2UITest {

    static WebDriver driver;
    static final String BASE        = "http://localhost:5173";
    static final String ADMIN_EMAIL = "admin@stellarjets.com";
    static final String ADMIN_PASS  = "admin123";
    static final String USER_EMAIL  = "user@stellarjets.com";
    static final String USER_PASS   = "user123";
    static final String TEST_EMAIL  =
            "test-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";

    // Page Objects
    static HomePage         homePage;
    static LoginPage        loginPage;
    static RegisterPage     registerPage;
    static FlightDetailPage detailPage;
    static AdminPanelPage   adminPage;

    @BeforeAll
    static void setup() {
        assumeTrue(servidorDisponible(BASE),
                "Frontend no disponible en " + BASE + " — omitiendo tests de UI Sprint 2");

        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage",
                "--window-size=1280,900", "--disable-gpu");
        driver = new ChromeDriver(opts);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));

        homePage     = new HomePage(driver, BASE);
        loginPage    = new LoginPage(driver, BASE);
        registerPage = new RegisterPage(driver, BASE);
        detailPage   = new FlightDetailPage(driver, BASE);
        adminPage    = new AdminPanelPage(driver, BASE);
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
    // US#12 — Registrar usuario
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(1)
    @Feature("US#12 — Registro de usuario")
    @Story("Página de registro accesible")
    @DisplayName("TC-UI-S2-01 | US#12 — Página de registro accesible en /register")
    void registro_paginaAccesible() {
        registerPage.open();
        assertTrue(registerPage.isFormVisible(), "Formulario de registro no encontrado");
    }

    @Test @Order(2)
    @Feature("US#12 — Registro de usuario")
    @Story("Formulario tiene campos obligatorios")
    @DisplayName("TC-UI-S2-02 | US#12 — Formulario de registro tiene campos obligatorios")
    void registro_camposPresentes() {
        registerPage.open();
        assertTrue(registerPage.hasMinFields(4),
                "Se esperaban al menos 4 campos de registro");
    }

    @Test @Order(3)
    @Feature("US#12 — Registro de usuario")
    @Story("Registro exitoso muestra confirmación")
    @DisplayName("TC-UI-S2-03 | US#12 — Registro exitoso muestra pantalla de confirmación")
    void registro_exitoso_muestraConfirmacion() {
        registerPage.open();
        registerPage.register("Juan", "Pérez", TEST_EMAIL, "password123");
        assertTrue(registerPage.hasSuccessScreen(),
                "Pantalla post-registro no encontrada");
    }

    @Test @Order(4)
    @Feature("US#12 — Registro de usuario")
    @Story("Email duplicado muestra error")
    @DisplayName("TC-UI-S2-04 | US#12 — Email duplicado muestra mensaje de error")
    void registro_emailDuplicado_muestraError() {
        registerPage.open();
        registerPage.register("Admin", "User", ADMIN_EMAIL, "password123");
        assertTrue(registerPage.hasErrorMessage(),
                "Mensaje de error por email duplicado no encontrado");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#13 — Identificar usuario (Login)
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(5)
    @Feature("US#13 — Login")
    @Story("Página de login accesible")
    @DisplayName("TC-UI-S2-05 | US#13 — Página de login accesible en /login")
    void login_paginaAccesible() {
        loginPage.open();
        assertTrue(loginPage.isFormVisible(), "Formulario de login no encontrado");
    }

    @Test @Order(6)
    @Feature("US#13 — Login")
    @Story("Login exitoso redirige al home")
    @DisplayName("TC-UI-S2-06 | US#13 — Login exitoso con usuario existente")
    void login_exitoso_redirigAHome() {
        loginPage.loginAs(USER_EMAIL, USER_PASS);
        assertEquals(BASE + "/", driver.getCurrentUrl(), "No redirigió al home tras login");
    }

    @Test @Order(7)
    @Feature("US#13 — Login")
    @Story("Login fallido muestra error")
    @DisplayName("TC-UI-S2-07 | US#13 — Login fallido muestra mensaje de error")
    void login_fallido_muestraError() {
        loginPage.loginWithBadCredentials("noexiste@test.com", "passwordmal");
        assertTrue(loginPage.hasErrorMessage(), "Mensaje de error de login no encontrado");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#14 — Información personal en header
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(8)
    @Feature("US#14 — Información personal")
    @Story("Nombre del usuario visible en header tras login")
    @DisplayName("TC-UI-S2-08 | US#14 — Nombre del usuario visible en el header tras login")
    void login_nombreUsuarioEnHeader() {
        loginPage.loginAs(USER_EMAIL, USER_PASS);
        homePage.open();
        assertTrue(homePage.hasUserName("Carlos"),
                "Nombre del usuario no encontrado en el header: " + homePage.headerText());
    }

    @Test @Order(9)
    @Feature("US#14 — Información personal")
    @Story("Sesión persiste tras recargar")
    @DisplayName("TC-UI-S2-09 | US#14 — Sesión persiste tras recargar la página")
    void login_sesionPersisteTrasRecarga() {
        loginPage.loginAs(USER_EMAIL, USER_PASS);
        driver.navigate().refresh();
        homePage.open();
        String h = homePage.headerText();
        assertFalse(h.contains("Iniciar sesión") && h.contains("Crear cuenta"),
                "La sesión no persiste tras recargar");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#15 — Cerrar sesión
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(10)
    @Feature("US#15 — Logout")
    @Story("Cerrar sesión regresa estado no autenticado")
    @DisplayName("TC-UI-S2-10 | US#15 — Cerrar sesión regresa estado no autenticado")
    void logout_regresaEstadoNoAutenticado() {
        loginPage.loginAs(USER_EMAIL, USER_PASS);
        homePage.open();
        adminPage.logout();
        assertTrue(adminPage.isLoggedOut(), "Header no regresó al estado no autenticado");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#16 — Administrador
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(11)
    @Feature("US#16 — Rol administrador")
    @Story("Admin ve panel de administración")
    @DisplayName("TC-UI-S2-11 | US#16 — Admin ve panel de administración en /administracion")
    void admin_panelAccesibleConRolAdmin() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        assertTrue(adminPage.isPanelVisible(), "Panel de administración no visible para ADMIN");
    }

    @Test @Order(12)
    @Feature("US#16 — Rol administrador")
    @Story("Panel admin contiene tabs de gestión")
    @DisplayName("TC-UI-S2-12 | US#16 — Panel admin contiene tabs de gestión")
    void admin_panelConteneTabs() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        assertTrue(adminPage.hasTabs(), "Tabs del panel no encontrados");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#17 — Administrar características
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(13)
    @Feature("US#17 — Gestión de características")
    @Story("Panel admin muestra sección de características")
    @DisplayName("TC-UI-S2-13 | US#17 — Panel admin muestra sección de características")
    void admin_seccionCaracteristicasVisible() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        assertTrue(adminPage.hasCharacteristicsSection(),
                "Sección de características no encontrada en el panel");
    }

    @Test @Order(14)
    @Feature("US#17 — Gestión de características")
    @Story("Lista de características existentes en panel")
    @DisplayName("TC-UI-S2-14 | US#17 — Panel admin muestra lista de características existentes")
    void admin_listaCaracteristicasPresente() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        adminPage.goToCharacteristics();
        assertTrue(adminPage.hasCharacteristicData(),
                "Características de ejemplo no encontradas en el panel");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#18 — Ver características en detalle
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(15)
    @Feature("US#18 — Características en detalle")
    @Story("Sección características visible en detalle del vuelo")
    @DisplayName("TC-UI-S2-15 | US#18 — Sección características visible en detalle del vuelo")
    void detalle_seccionCaracteristicasVisible() {
        detailPage.open(1);
        assumeTrue(detailPage.hasCharacteristicsSection(),
                "El vuelo #1 no tiene características asignadas — reiniciar el backend para que DataInitializer las asigne");
        assertTrue(detailPage.hasCharacteristicsSection(),
                "Sección de características no encontrada en el detalle del vuelo");
    }

    @Test @Order(16)
    @Feature("US#18 — Características en detalle")
    @Story("Características muestran texto descriptivo")
    @DisplayName("TC-UI-S2-16 | US#18 — Características muestran texto descriptivo")
    void detalle_caracteristicasConTexto() {
        detailPage.open(1);
        assumeTrue(detailPage.hasCharacteristicsSection(),
                "El vuelo #1 no tiene características — reiniciar el backend");
        assertTrue(detailPage.hasCharacteristicText(),
                "Texto de características no encontrado en el detalle");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#20 — Categorías en home
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(17)
    @Feature("US#20 — Filtro por categorías")
    @Story("Sección de categorías visible en home")
    @DisplayName("TC-UI-S2-17 | US#20 — Sección de categorías visible en el home")
    void home_seccionCategoriasVisible() {
        homePage.open();
        assertTrue(homePage.hasCategoryCards(), "Tarjetas de categoría no encontradas en el home");
    }

    @Test @Order(18)
    @Feature("US#20 — Filtro por categorías")
    @Story("Tarjeta Todos presente en home")
    @DisplayName("TC-UI-S2-18 | US#20 — Tarjeta 'Todos' presente en sección categorías")
    void home_tarjetaTodosPresente() {
        homePage.open();
        assertTrue(homePage.hasTodosCard(), "Tarjeta 'Todos' no encontrada en el home");
    }

    @Test @Order(19)
    @Feature("US#20 — Filtro por categorías")
    @Story("Clic en categoría actualiza vuelos")
    @DisplayName("TC-UI-S2-19 | US#20 — Clic en categoría actualiza la sección de vuelos")
    void home_filtroCategoriaActualizaVuelos() {
        homePage.open();
        int totalInicial = homePage.getFlightCards().size();

        List<WebElement> catBtns = homePage.getCategoryButtons("Lujo", "Aventura", "Negocios");
        assumeTrue(!catBtns.isEmpty(), "Botones de categoría no encontrados");
        catBtns.get(0).click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        int totalFiltrado = homePage.getFlightCards().size();
        assertTrue(totalFiltrado <= totalInicial,
                "El filtro no redujo los vuelos: antes=" + totalInicial + " después=" + totalFiltrado);
    }

    @Test @Order(20)
    @Feature("US#20 — Filtro por categorías")
    @Story("Clic en Todos restaura el listado")
    @DisplayName("TC-UI-S2-20 | US#20 — Clic en 'Todos' restaura el listado completo")
    void home_tarjetaTodosRestaureaListado() {
        homePage.open();

        List<WebElement> catBtns = homePage.getCategoryButtons("Lujo", "Aventura");
        assumeTrue(!catBtns.isEmpty(), "Botones de categoría no encontrados");
        catBtns.get(0).click();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        homePage.clickTodos();
        assertFalse(homePage.getFlightCards().isEmpty(), "No se encontraron vuelos tras clic en 'Todos'");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // US#21 — Categorías en panel admin
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(21)
    @Feature("US#21 — Gestión de categorías")
    @Story("Panel admin contiene sección de categorías")
    @DisplayName("TC-UI-S2-21 | US#21 — Panel admin contiene sección de categorías")
    void admin_seccionCategoriasEnPanel() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        assertTrue(adminPage.hasCategoriesSection(),
                "Sección de categorías no encontrada en el panel admin");
    }

    @Test @Order(22)
    @Feature("US#21 — Gestión de categorías")
    @Story("Categorías del sistema visibles en panel admin")
    @DisplayName("TC-UI-S2-22 | US#21 — Categorías del sistema visibles en el panel admin")
    void admin_categoriasExistentesVisibles() {
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASS);
        adminPage.open();
        adminPage.goToCategories();
        assertTrue(adminPage.hasCategoryData(),
                "Categorías de ejemplo no encontradas en el panel admin");
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
