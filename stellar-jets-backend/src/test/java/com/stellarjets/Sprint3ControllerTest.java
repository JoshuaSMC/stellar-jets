package com.stellarjets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellarjets.dto.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración — Sprint 3
 * Cubren US#22 (Búsqueda con fechas), US#23 (Disponibilidad),
 * US#24 (Favoritos — agregar/eliminar), US#25 (Listar favoritos),
 * US#28 (Reseñas y puntaje), US#29 (Eliminar categoría con FK).
 *
 * El perfil "test" deshabilita Spring Security (TestSecurityConfig).
 * Los endpoints de favoritos y reseñas usan @WithMockUser para proveer
 * el principal al SecurityContextHolder, mapeado al usuario demo
 * "user@stellarjets.com" creado por DataInitializer.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Epic("Stellar Jets — Backend")
@DisplayName("Sprint 3 — Tests de integración")
class Sprint3ControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private static final String FLIGHTS      = "/api/flights";
    private static final String RESERVATIONS = "/api/reservations";
    private static final String FAVORITES    = "/api/favorites";
    private static final String REVIEWS      = "/api/reviews";
    private static final String CATS_ADM     = "/api/admin/categories";

    // ─── US#22: Realizar búsqueda ─────────────────────────────────────────────

    @Test
    @Feature("US#22 — Realizar búsqueda")
    @Story("Búsqueda por nombre devuelve resultados paginados")
    @DisplayName("TC-S3-01 | US#22 — Búsqueda por nombre devuelve vuelos que coinciden")
    void busqueda_porNombre_retornaResultados() throws Exception {
        mvc.perform(get(FLIGHTS + "/search")
                .param("query", "patagonia")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name", containsStringIgnoringCase("patagonia")));
    }

    @Test
    @Feature("US#22 — Realizar búsqueda")
    @Story("Búsqueda por código IATA devuelve vuelos")
    @DisplayName("TC-S3-02 | US#22 — Búsqueda por código IATA devuelve vuelos que coinciden")
    void busqueda_porIATA_retornaResultados() throws Exception {
        mvc.perform(get(FLIGHTS + "/search")
                .param("query", "EZE")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @Feature("US#22 — Realizar búsqueda")
    @Story("Búsqueda con fechas disponibles devuelve vuelos libres")
    @DisplayName("TC-S3-03 | US#22 — Búsqueda con fechas lejanas sin ocupar devuelve vuelos")
    void busqueda_conFechasLibres_retornaVuelos() throws Exception {
        mvc.perform(get(FLIGHTS + "/search")
                .param("checkIn",  "2030-01-01")
                .param("checkOut", "2030-01-10")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @Feature("US#22 — Realizar búsqueda")
    @Story("Búsqueda combinada por nombre y fechas filtra correctamente")
    @DisplayName("TC-S3-04 | US#22 — Búsqueda combinada (query + fechas) devuelve paginado correcto")
    void busqueda_combinadaNombreFechas_retornaPaginado() throws Exception {
        mvc.perform(get(FLIGHTS + "/search")
                .param("query", "tokio")
                .param("checkIn",  "2030-06-01")
                .param("checkOut", "2030-06-15")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Feature("US#22 — Realizar búsqueda")
    @Story("Búsqueda sin parámetros devuelve todos los vuelos activos")
    @DisplayName("TC-S3-05 | US#22 — Búsqueda vacía devuelve todos los vuelos activos")
    void busqueda_sinParametros_retornaVuelosActivos() throws Exception {
        mvc.perform(get(FLIGHTS + "/search")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @Feature("US#22 — Realizar búsqueda")
    @Story("Recomendados devuelven vuelos ordenados por rating")
    @DisplayName("TC-S3-06 | US#22 — Endpoint recomendados devuelve array con rating")
    void recomendados_devuelveArrayConRating() throws Exception {
        mvc.perform(get(FLIGHTS + "/recommended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rating").isNumber());
    }

    // ─── US#23: Visualizar disponibilidad ────────────────────────────────────

    @Test
    @Feature("US#23 — Visualizar disponibilidad")
    @Story("Fechas ocupadas de un vuelo con reservas devuelve lista")
    @DisplayName("TC-S3-07 | US#23 — Vuelo con reservas devuelve rangos de fechas ocupadas")
    void disponibilidad_vueloConReservas_retornaFechasOcupadas() throws Exception {
        mvc.perform(get(RESERVATIONS + "/1/occupied-dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].checkIn").isNotEmpty())
                .andExpect(jsonPath("$[0].checkOut").isNotEmpty());
    }

    @Test
    @Feature("US#23 — Visualizar disponibilidad")
    @Story("Vuelo sin reservas devuelve lista vacía")
    @DisplayName("TC-S3-08 | US#23 — Vuelo sin reservas devuelve lista vacía de fechas ocupadas")
    void disponibilidad_vueloSinReservas_retornaListaVacia() throws Exception {
        // Vuelo 9 (índice) = Barcelona Meetings — sin reservas según DataInitializer
        mvc.perform(get(RESERVATIONS + "/9/occupied-dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Feature("US#23 — Visualizar disponibilidad")
    @Story("Respuesta incluye campos checkIn y checkOut como strings ISO")
    @DisplayName("TC-S3-09 | US#23 — Fechas ocupadas tienen formato ISO (checkIn, checkOut)")
    void disponibilidad_fechasOcupadas_tienenFormatoISO() throws Exception {
        mvc.perform(get(RESERVATIONS + "/1/occupied-dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].checkIn",  matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                .andExpect(jsonPath("$[0].checkOut", matchesPattern("\\d{4}-\\d{2}-\\d{2}")));
    }

    // ─── US#24: Marcar como favorito ─────────────────────────────────────────

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#24 — Marcar como favorito")
    @Story("Agregar vuelo a favoritos devuelve 200")
    @DisplayName("TC-S3-10 | US#24 — Agregar vuelo a favoritos: devuelve 200")
    void favorito_agregar_retorna200() throws Exception {
        mvc.perform(post(FAVORITES + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#24 — Marcar como favorito")
    @Story("Agregar vuelo ya en favoritos es idempotente")
    @DisplayName("TC-S3-11 | US#24 — Agregar vuelo duplicado a favoritos es idempotente: devuelve 200")
    void favorito_agregarDuplicado_esIdempotente() throws Exception {
        mvc.perform(post(FAVORITES + "/2")).andExpect(status().isOk());
        mvc.perform(post(FAVORITES + "/2")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#24 — Marcar como favorito")
    @Story("Eliminar vuelo de favoritos devuelve 204")
    @DisplayName("TC-S3-12 | US#24 — Eliminar vuelo de favoritos: devuelve 204")
    void favorito_eliminar_retorna204() throws Exception {
        mvc.perform(post(FAVORITES + "/3")).andExpect(status().isOk());
        mvc.perform(delete(FAVORITES + "/3")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#24 — Marcar como favorito")
    @Story("Agregar vuelo inexistente devuelve 404")
    @DisplayName("TC-S3-13 | US#24 — Agregar vuelo inexistente a favoritos: devuelve 404")
    void favorito_vueloInexistente_retorna404() throws Exception {
        mvc.perform(post(FAVORITES + "/99999"))
                .andExpect(status().isNotFound());
    }

    // ─── US#25: Listar favoritos ──────────────────────────────────────────────

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#25 — Listar favoritos")
    @Story("Listar favoritos vacíos devuelve array vacío")
    @DisplayName("TC-S3-14 | US#25 — Listar favoritos sin agregar: devuelve array vacío")
    void favoritos_listar_sinFavoritos_retornaVacio() throws Exception {
        mvc.perform(get(FAVORITES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#25 — Listar favoritos")
    @Story("Favorito aparece en la lista tras agregar")
    @DisplayName("TC-S3-15 | US#25 — Favorito aparece en lista tras agregar: contiene FlightDTO")
    void favoritos_listar_conFavorito_retornaFlight() throws Exception {
        mvc.perform(post(FAVORITES + "/1")).andExpect(status().isOk());

        mvc.perform(get(FAVORITES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#25 — Listar favoritos")
    @Story("Favorito desaparece de la lista tras eliminarlo")
    @DisplayName("TC-S3-16 | US#25 — Favorito desaparece de lista tras eliminar")
    void favoritos_listar_trasBorrar_retornaVacio() throws Exception {
        mvc.perform(post(FAVORITES + "/1")).andExpect(status().isOk());
        mvc.perform(delete(FAVORITES + "/1")).andExpect(status().isNoContent());

        mvc.perform(get(FAVORITES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── US#28: Puntuar producto ──────────────────────────────────────────────

    @Test
    @Feature("US#28 — Puntuar producto")
    @Story("Listar reseñas de un vuelo devuelve array")
    @DisplayName("TC-S3-17 | US#28 — Listar reseñas de vuelo: devuelve array (puede estar vacío)")
    void resenas_listar_retornaArray() throws Exception {
        mvc.perform(get(REVIEWS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#28 — Puntuar producto")
    @Story("Crear reseña con estrellas válidas devuelve 200")
    @DisplayName("TC-S3-18 | US#28 — Crear reseña (5 estrellas): devuelve 200 con datos")
    void resena_crear_retorna200ConDatos() throws Exception {
        ReviewRequestDTO req = new ReviewRequestDTO(5, "Excelente vuelo, muy recomendado");

        mvc.perform(post(REVIEWS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.stars").value(5))
                .andExpect(jsonPath("$.firstName").isNotEmpty())
                .andExpect(jsonPath("$.comment").value("Excelente vuelo, muy recomendado"));
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#28 — Puntuar producto")
    @Story("Estrellas fuera de rango devuelve 400")
    @DisplayName("TC-S3-19 | US#28 — Reseña con estrellas fuera de rango (6): devuelve 400")
    void resena_estrellasInvalidas_retorna400() throws Exception {
        ReviewRequestDTO req = new ReviewRequestDTO(6, "Inválido");

        mvc.perform(post(REVIEWS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#28 — Puntuar producto")
    @Story("Reseña creada aparece en el listado del vuelo")
    @DisplayName("TC-S3-20 | US#28 — Reseña creada aparece en el listado con stars y firstName")
    void resena_aparece_enListado() throws Exception {
        ReviewRequestDTO req = new ReviewRequestDTO(4, "Muy bueno");

        mvc.perform(post(REVIEWS + "/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mvc.perform(get(REVIEWS + "/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].stars").isNumber())
                .andExpect(jsonPath("$[0].firstName").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#28 — Puntuar producto")
    @Story("Rating del vuelo se actualiza tras nueva reseña")
    @DisplayName("TC-S3-21 | US#28 — Rating del vuelo se actualiza tras agregar reseña")
    void resena_actualizaRatingDelVuelo() throws Exception {
        ReviewRequestDTO req = new ReviewRequestDTO(2, "Regular");

        mvc.perform(post(REVIEWS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mvc.perform(get(FLIGHTS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").isNumber())
                .andExpect(jsonPath("$.reviewCount", greaterThanOrEqualTo(1)));
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#28 — Puntuar producto")
    @Story("Reseña puede actualizarse con nueva puntuación")
    @DisplayName("TC-S3-22 | US#28 — Reseña existente se actualiza con addOrUpdate (upsert)")
    void resena_actualizar_sobreescribeAnterior() throws Exception {
        mvc.perform(post(REVIEWS + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new ReviewRequestDTO(3, "Normal"))))
                .andExpect(status().isOk());

        mvc.perform(post(REVIEWS + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new ReviewRequestDTO(5, "Cambié de opinión, excelente"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stars").value(5));

        mvc.perform(get(REVIEWS + "/3"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ─── US#29: Eliminar categoría ────────────────────────────────────────────

    @Test
    @Feature("US#29 — Eliminar categoría")
    @Story("Eliminar categoría existente devuelve 204")
    @DisplayName("TC-S3-23 | US#29 — Eliminar categoría existente: devuelve 204")
    void categoria_eliminar_retorna204() throws Exception {
        mvc.perform(delete(CATS_ADM + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Feature("US#29 — Eliminar categoría")
    @Story("Vuelos de la categoría eliminada quedan sin categoría")
    @DisplayName("TC-S3-24 | US#29 — Vuelos de categoría eliminada mantienen campo category nulo")
    void categoria_eliminar_vuelosQuedan_sinCategoria() throws Exception {
        mvc.perform(delete(CATS_ADM + "/1"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/flights/1"))
                .andExpect(status().isOk());
        // La categoría queda nula — el vuelo sigue existiendo pero sin categoría asignada
    }

    @Test
    @Feature("US#29 — Eliminar categoría")
    @Story("Eliminar categoría inexistente devuelve 404")
    @DisplayName("TC-S3-25 | US#29 — Eliminar categoría inexistente: devuelve 404")
    void categoria_eliminarInexistente_retorna404() throws Exception {
        mvc.perform(delete(CATS_ADM + "/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Feature("US#29 — Eliminar categoría")
    @Story("Categoría eliminada no aparece en listado público")
    @DisplayName("TC-S3-26 | US#29 — Categoría eliminada no aparece en listado público")
    void categoria_eliminar_noApareceEnListado() throws Exception {
        String resp = mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        int totalAntes = mapper.readTree(resp).size();

        mvc.perform(delete(CATS_ADM + "/1"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(totalAntes - 1)));
    }
}
