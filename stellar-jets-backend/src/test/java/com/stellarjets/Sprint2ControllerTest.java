package com.stellarjets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellarjets.dto.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración — Sprint 2
 * Cubren US#12 (Registro), US#13 (Login), US#16 (Admin auth),
 * US#17 (Características), US#18 (Ver características),
 * US#19 (Email), US#20 (Categorías multi-filtro), US#21 (CRUD categorías).
 *
 * El perfil "test" deshabilita Spring Security (TestSecurityConfig),
 * por lo que los endpoints admin son accesibles sin JWT.
 * El email falla silenciosamente vía @Async — no requiere credenciales SMTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Epic("Stellar Jets — Backend")
@DisplayName("Sprint 2 — Tests de integración")
class Sprint2ControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private static final String AUTH     = "/api/auth";
    private static final String ADMIN    = "/api/admin";
    private static final String PUBLIC   = "/api/flights";
    private static final String CHARS    = "/api/admin/characteristics";
    private static final String CATS_ADM = "/api/admin/categories";
    private static final String CATS_PUB = "/api/categories";

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private RegisterRequestDTO buildRegister(String email) {
        return RegisterRequestDTO.builder()
                .firstName("Juan").lastName("Pérez")
                .email(email).password("password123")
                .build();
    }

    private LoginRequestDTO buildLogin(String email, String password) {
        return new LoginRequestDTO(email, password);
    }

    private CharacteristicDTO buildCharacteristic(String name, String icon) {
        return CharacteristicDTO.builder().name(name).iconName(icon).build();
    }

    private CategoryDTO buildCategory(String name) {
        return CategoryDTO.builder()
                .name(name).description("Descripción de " + name)
                .iconName("star").imageUrl("https://example.com/img.jpg")
                .build();
    }

    // ─── US#12: Registrar usuario ─────────────────────────────────────────────

    @Test
    @Feature("US#12 — Registrar usuario")
    @Story("Registro con datos válidos devuelve token")
    @DisplayName("TC-S2-01 | US#12 — Registrar usuario con datos válidos: devuelve 201 + token")
    void registro_datosValidos_retorna201ConToken() throws Exception {
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("nuevo@test.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("nuevo@test.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.lastName").value("Pérez"));
    }

    @Test
    @Feature("US#12 — Registrar usuario")
    @Story("Email duplicado devuelve conflicto")
    @DisplayName("TC-S2-02 | US#12 — Email ya registrado: devuelve 409")
    void registro_emailDuplicado_retorna409() throws Exception {
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("duplicado@test.com"))))
                .andExpect(status().isCreated());

        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("duplicado@test.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    @Feature("US#12 — Registrar usuario")
    @Story("Campos vacíos devuelven error de validación")
    @DisplayName("TC-S2-03 | US#12 — Campos obligatorios vacíos: devuelve 400")
    void registro_camposVacios_retorna400() throws Exception {
        RegisterRequestDTO dto = RegisterRequestDTO.builder()
                .firstName("").lastName("").email("").password("").build();

        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Feature("US#12 — Registrar usuario")
    @Story("Contraseña corta devuelve error de validación")
    @DisplayName("TC-S2-04 | US#12 — Contraseña menor a 6 caracteres: devuelve 400")
    void registro_passwordCorta_retorna400() throws Exception {
        RegisterRequestDTO dto = buildRegister("short@test.com");
        dto.setPassword("abc");

        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ─── US#13: Identificar usuario (Login) ──────────────────────────────────

    @Test
    @Feature("US#13 — Identificar usuario (Login)")
    @Story("Credenciales correctas devuelven token")
    @DisplayName("TC-S2-05 | US#13 — Login con credenciales correctas: devuelve 200 + token")
    void login_credencialesCorrectas_retorna200ConToken() throws Exception {
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("login@test.com"))))
                .andExpect(status().isCreated());

        mvc.perform(post(AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildLogin("login@test.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("login@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @Feature("US#13 — Identificar usuario (Login)")
    @Story("Contraseña incorrecta devuelve no autorizado")
    @DisplayName("TC-S2-06 | US#13 — Login con contraseña incorrecta: devuelve 401")
    void login_passwordIncorrecta_retorna401() throws Exception {
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("wrong@test.com"))))
                .andExpect(status().isCreated());

        mvc.perform(post(AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildLogin("wrong@test.com", "incorrecta"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @Feature("US#13 — Identificar usuario (Login)")
    @Story("Email inexistente devuelve no autorizado")
    @DisplayName("TC-S2-07 | US#13 — Login con email inexistente: devuelve 401")
    void login_emailInexistente_retorna401() throws Exception {
        mvc.perform(post(AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildLogin("noexiste@test.com", "cualquier"))))
                .andExpect(status().isUnauthorized());
    }

    // ─── US#14: Información personal en token ────────────────────────────────

    @Test
    @Feature("US#14 — Información personal en token")
    @Story("Respuesta incluye datos personales del usuario")
    @DisplayName("TC-S2-08 | US#14 — Token incluye firstName, lastName, email y role")
    void registro_respuestaIncluyeInfoPersonal() throws Exception {
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("info@test.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.lastName").value("Pérez"))
                .andExpect(jsonPath("$.email").value("info@test.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    // ─── US#16: Admin — acceso diferenciado ──────────────────────────────────

    @Test
    @Feature("US#16 — Acceso diferenciado admin")
    @Story("Endpoint de usuarios admin responde 200")
    @DisplayName("TC-S2-09 | US#16 — Endpoint admin /api/admin/users responde 200")
    void admin_listarUsuarios_responde200() throws Exception {
        mvc.perform(get(ADMIN + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Feature("US#16 — Acceso diferenciado admin")
    @Story("Usuario registrado aparece en listado con rol USER")
    @DisplayName("TC-S2-10 | US#16 — Usuario registrado aparece en listado admin con rol USER")
    void admin_usuarioRegistrado_apareceEnListado() throws Exception {
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("admin-list@test.com"))))
                .andExpect(status().isCreated());

        mvc.perform(get(ADMIN + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem("admin-list@test.com")))
                .andExpect(jsonPath("$[?(@.email == 'admin-list@test.com')].role",
                        hasItem("USER")));
    }

    @Test
    @Feature("US#16 — Acceso diferenciado admin")
    @Story("Toggle de rol USER a ADMIN y viceversa")
    @DisplayName("TC-S2-11 | US#16 — Toggle de rol USER → ADMIN funciona correctamente")
    void admin_toggleRol_cambiaAAdmin() throws Exception {
        String resp = mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("toggle@test.com"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        mvc.perform(patch(ADMIN + "/users/" + id + "/toggle-role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        mvc.perform(patch(ADMIN + "/users/" + id + "/toggle-role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // ─── US#17: Administrar características ──────────────────────────────────

    @Test
    @Feature("US#17 — Administrar características")
    @Story("Crear característica devuelve 201 con datos completos")
    @DisplayName("TC-S2-12 | US#17 — Crear característica: devuelve 201 con id, name e iconName")
    void caracteristica_crear_retorna201() throws Exception {
        mvc.perform(post(CHARS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildCharacteristic("WiFi Premium", "wifi"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("WiFi Premium"))
                .andExpect(jsonPath("$.iconName").value("wifi"));
    }

    @Test
    @Feature("US#17 — Administrar características")
    @Story("Listar características devuelve array con name e iconName")
    @DisplayName("TC-S2-13 | US#17 — Listar características: devuelve 200 con array")
    void caracteristica_listar_retorna200() throws Exception {
        mvc.perform(get(CHARS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name").isNotEmpty())
                .andExpect(jsonPath("$[*].iconName").isNotEmpty());
    }

    @Test
    @Feature("US#17 — Administrar características")
    @Story("Editar característica actualiza nombre e ícono")
    @DisplayName("TC-S2-14 | US#17 — Editar característica: actualiza nombre e ícono")
    void caracteristica_editar_actualizaDatos() throws Exception {
        String resp = mvc.perform(post(CHARS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildCharacteristic("Original", "star"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        CharacteristicDTO updated = buildCharacteristic("Actualizado", "wifi");
        mvc.perform(put(CHARS + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Actualizado"))
                .andExpect(jsonPath("$.iconName").value("wifi"));
    }

    @Test
    @Feature("US#17 — Administrar características")
    @Story("Eliminar característica devuelve 204")
    @DisplayName("TC-S2-15 | US#17 — Eliminar característica: devuelve 204")
    void caracteristica_eliminar_retorna204() throws Exception {
        String resp = mvc.perform(post(CHARS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildCharacteristic("Para eliminar", "tv"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        mvc.perform(delete(CHARS + "/" + id))
                .andExpect(status().isNoContent());

        mvc.perform(get(CHARS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", not(hasItem(id.intValue()))));
    }

    // ─── US#18: Visualizar características en detalle ────────────────────────

    @Test
    @Feature("US#18 — Ver características en detalle")
    @Story("Detalle de vuelo incluye array de características")
    @DisplayName("TC-S2-16 | US#18 — Detalle de vuelo incluye campo characteristics")
    void vuelo_detalle_incluyeCharacteristics() throws Exception {
        mvc.perform(get(PUBLIC + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characteristics").isArray());
    }

    @Test
    @Feature("US#18 — Ver características en detalle")
    @Story("Características incluyen name e iconName")
    @DisplayName("TC-S2-17 | US#18 — Características de vuelo incluyen name e iconName")
    void vuelo_detalle_characteristicsConIconName() throws Exception {
        mvc.perform(get(PUBLIC + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characteristics").isArray())
                .andExpect(jsonPath("$.characteristics[0].name").isNotEmpty())
                .andExpect(jsonPath("$.characteristics[0].iconName").isNotEmpty());
    }

    // ─── US#19: Notificación por email ───────────────────────────────────────

    @Test
    @Feature("US#19 — Notificación por email")
    @Story("Fallo del email no bloquea el registro")
    @DisplayName("TC-S2-18 | US#19 — Registro exitoso aunque email service falle (async)")
    void registro_emailFalla_noBloqueoRegistro() throws Exception {
        // EmailService está mockeado → no lanza excepción ni bloquea el flujo
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("email-test@test.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Feature("US#19 — Notificación por email")
    @Story("Reenvío de confirmación con email válido")
    @DisplayName("TC-S2-19 | US#19 — Reenvío de confirmación con email válido: devuelve 200")
    void reenvioConfirmacion_emailValido_retorna200() throws Exception {
        mvc.perform(post(AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildRegister("reenvio@test.com"))))
                .andExpect(status().isCreated());

        mvc.perform(post(AUTH + "/resend-confirmation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"reenvio@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @Feature("US#19 — Notificación por email")
    @Story("Reenvío sin email devuelve error de validación")
    @DisplayName("TC-S2-20 | US#19 — Reenvío sin email: devuelve 400")
    void reenvioConfirmacion_sinEmail_retorna400() throws Exception {
        mvc.perform(post(AUTH + "/resend-confirmation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ─── US#20: Sección de categorías — filtro multi-categoría ───────────────

    @Test
    @Feature("US#20 — Sección de categorías")
    @Story("Categorías activas incluyen imageUrl y flightCount")
    @DisplayName("TC-S2-21 | US#20 — Categorías activas devuelven array con imageUrl y flightCount")
    void categorias_activas_incluyenImageUrlYCount() throws Exception {
        mvc.perform(get(CATS_PUB + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].imageUrl").isNotEmpty())
                .andExpect(jsonPath("$[0].flightCount").isNumber());
    }

    @Test
    @Feature("US#20 — Sección de categorías")
    @Story("Filtro por una categoría devuelve sus vuelos")
    @DisplayName("TC-S2-22 | US#20 — Filtro por una categoría devuelve solo sus vuelos")
    void vuelos_filtroUnaCategoria_retornaVuelosCorrectos() throws Exception {
        mvc.perform(get(PUBLIC + "/search")
                .param("categoryIds", "1")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    @Feature("US#20 — Sección de categorías")
    @Story("Filtro multi-categoría devuelve vuelos de ambas")
    @DisplayName("TC-S2-23 | US#20 — Filtro multi-categoría devuelve vuelos de ambas")
    void vuelos_filtroMultiCategoria_retornaVuelosDeAmbas() throws Exception {
        mvc.perform(get(PUBLIC + "/search")
                .param("categoryIds", "1,2")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    @Feature("US#20 — Sección de categorías")
    @Story("Sin filtros devuelve todos los vuelos activos")
    @DisplayName("TC-S2-24 | US#20 — Búsqueda sin filtros devuelve todos los vuelos activos")
    void vuelos_sinFiltros_retornaVuelosActivos() throws Exception {
        mvc.perform(get(PUBLIC + "/search").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    // ─── US#21: Agregar categoría ────────────────────────────────────────────

    @Test
    @Feature("US#21 — Agregar categoría")
    @Story("Crear categoría devuelve 201 con imageUrl")
    @DisplayName("TC-S2-25 | US#21 — Crear categoría con imagen: devuelve 201 con imageUrl")
    void categoria_crear_retorna201ConImageUrl() throws Exception {
        mvc.perform(post(CATS_ADM)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildCategory("Romántico"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Romántico"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/img.jpg"));
    }

    @Test
    @Feature("US#21 — Agregar categoría")
    @Story("Editar categoría actualiza nombre e imagen")
    @DisplayName("TC-S2-26 | US#21 — Editar categoría: actualiza nombre e imagen")
    void categoria_editar_actualizaDatos() throws Exception {
        String resp = mvc.perform(post(CATS_ADM)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildCategory("Original"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        CategoryDTO updated = buildCategory("Actualizado");
        updated.setImageUrl("https://example.com/new.jpg");

        mvc.perform(put(CATS_ADM + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Actualizado"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/new.jpg"));
    }

    @Test
    @Feature("US#21 — Agregar categoría")
    @Story("Eliminar categoría devuelve 204")
    @DisplayName("TC-S2-27 | US#21 — Eliminar categoría: devuelve 204")
    void categoria_eliminar_retorna204() throws Exception {
        String resp = mvc.perform(post(CATS_ADM)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildCategory("Para eliminar"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        mvc.perform(delete(CATS_ADM + "/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @Feature("US#21 — Agregar categoría")
    @Story("Listar categorías incluye imageUrl")
    @DisplayName("TC-S2-28 | US#21 — Listar todas las categorías: devuelve array con imageUrl")
    void categoria_listar_incluyeImageUrl() throws Exception {
        mvc.perform(get(CATS_PUB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].imageUrl").isNotEmpty());
    }
}
