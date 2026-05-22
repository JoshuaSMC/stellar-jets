package com.stellarjets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellarjets.dto.ReservationRequestDTO;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración — Sprint 4
 * Cubren US#30 (Seleccionar fecha), US#31 (Visualizar detalles),
 * US#32 (Realizar reserva), US#33 (Historial de reservas).
 * US#34 (WhatsApp) y US#35 (Email) son funcionalidades de frontend/async
 * verificadas en Sprint4UITest y manualmente en Mailtrap respectivamente.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Epic("Stellar Jets — Backend")
@DisplayName("Sprint 4 — Tests de integración")
class Sprint4ControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private static final String FLIGHTS      = "/api/flights";
    private static final String RESERVATIONS = "/api/reservations";

    // ─── US#30: Seleccionar fecha ─────────────────────────────────────────────

    @Test
    @Feature("US#30 — Seleccionar fecha")
    @Story("Fechas ocupadas de un vuelo se devuelven correctamente")
    @DisplayName("TC-S4-01 | US#30 — Fechas ocupadas del vuelo 1: devuelve array con checkIn y checkOut")
    void fechasOcupadas_vuelo1_retornaRangos() throws Exception {
        mvc.perform(get(RESERVATIONS + "/1/occupied-dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].checkIn",  matchesPattern("\\d{4}-\\d{2}-\\d{2}")))
                .andExpect(jsonPath("$[0].checkOut", matchesPattern("\\d{4}-\\d{2}-\\d{2}")));
    }

    @Test
    @Feature("US#30 — Seleccionar fecha")
    @Story("Endpoint de fechas ocupadas es público — no requiere autenticación")
    @DisplayName("TC-S4-02 | US#30 — Fechas ocupadas accesibles sin autenticación: devuelve 200")
    void fechasOcupadas_sinAuth_retorna200() throws Exception {
        mvc.perform(get(RESERVATIONS + "/1/occupied-dates"))
                .andExpect(status().isOk());
    }

    @Test
    @Feature("US#30 — Seleccionar fecha")
    @Story("Búsqueda por fechas libres devuelve vuelos disponibles")
    @DisplayName("TC-S4-03 | US#30 — Búsqueda por fechas lejanas sin ocupar devuelve vuelos disponibles")
    void busqueda_porFechasLibres_retornaVuelos() throws Exception {
        mvc.perform(get(FLIGHTS + "/search")
                .param("checkIn",  "2030-01-01")
                .param("checkOut", "2030-01-10")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @Feature("US#30 — Seleccionar fecha")
    @Story("Creación de reserva sin autenticación es rechazada")
    @DisplayName("TC-S4-04 | US#30 — Crear reserva sin autenticación: devuelve 403 Forbidden")
    void crearReserva_sinAuth_esRechazada() throws Exception {
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setCheckIn(LocalDate.now().plusDays(10));
        req.setCheckOut(LocalDate.now().plusDays(15));

        mvc.perform(post(RESERVATIONS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#30 — Seleccionar fecha")
    @Story("Rango de fechas solapado con reserva existente devuelve 409")
    @DisplayName("TC-S4-05 | US#30 — Fechas solapadas con reserva existente: devuelve 409 Conflict")
    void crearReserva_fechasSolapadas_retorna409() throws Exception {
        // Primero crear una reserva
        ReservationRequestDTO primera = new ReservationRequestDTO();
        primera.setCheckIn(LocalDate.of(2026, 8, 1));
        primera.setCheckOut(LocalDate.of(2026, 8, 10));

        mvc.perform(post(RESERVATIONS + "/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(primera)))
                .andExpect(status().isCreated());

        // Intentar reservar fechas que se solapan
        ReservationRequestDTO solapada = new ReservationRequestDTO();
        solapada.setCheckIn(LocalDate.of(2026, 8, 5));
        solapada.setCheckOut(LocalDate.of(2026, 8, 15));

        mvc.perform(post(RESERVATIONS + "/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(solapada)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#30 — Seleccionar fecha")
    @Story("Fecha de salida anterior o igual a la de entrada devuelve 400")
    @DisplayName("TC-S4-06 | US#30 — checkOut ≤ checkIn: devuelve 400 Bad Request")
    void crearReserva_checkOutAnteriorCheckIn_retorna400() throws Exception {
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setCheckIn(LocalDate.of(2026, 9, 10));
        req.setCheckOut(LocalDate.of(2026, 9, 5));  // salida antes de entrada

        mvc.perform(post(RESERVATIONS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ─── US#31: Visualizar detalles del producto ──────────────────────────────

    @Test
    @Feature("US#31 — Visualizar detalles")
    @Story("Detalle del vuelo incluye campos completos del producto")
    @DisplayName("TC-S4-07 | US#31 — Detalle del vuelo 1: incluye name, description, origin, destination")
    void detalleVuelo_incluyeCamposCompletos() throws Exception {
        mvc.perform(get(FLIGHTS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.origin.city").isNotEmpty())
                .andExpect(jsonPath("$.destination.city").isNotEmpty());
    }

    @Test
    @Feature("US#31 — Visualizar detalles")
    @Story("Detalle del vuelo incluye imágenes y precio")
    @DisplayName("TC-S4-08 | US#31 — Detalle del vuelo: incluye images array y price")
    void detalleVuelo_incluyeImagenesYPrecio() throws Exception {
        mvc.perform(get(FLIGHTS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.price", greaterThan(0.0)));
    }

    @Test
    @Feature("US#31 — Visualizar detalles")
    @Story("Detalle del vuelo incluye características y categoría")
    @DisplayName("TC-S4-09 | US#31 — Detalle del vuelo: incluye characteristics array y coverImageUrl")
    void detalleVuelo_incluyeCaracteristicasYCover() throws Exception {
        mvc.perform(get(FLIGHTS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characteristics").isArray())
                .andExpect(jsonPath("$.coverImageUrl").exists());
    }

    @Test
    @Feature("US#31 — Visualizar detalles")
    @Story("Detalle del vuelo incluye rating y asientos disponibles")
    @DisplayName("TC-S4-10 | US#31 — Detalle del vuelo: incluye rating y availableSeats")
    void detalleVuelo_incluyeRatingYAsientos() throws Exception {
        mvc.perform(get(FLIGHTS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").isNumber())
                .andExpect(jsonPath("$.availableSeats").isNumber());
    }

    // ─── US#32: Realizar reserva ──────────────────────────────────────────────

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#32 — Realizar reserva")
    @Story("Crear reserva válida devuelve 201 con datos completos")
    @DisplayName("TC-S4-11 | US#32 — Reserva válida: devuelve 201 con id, flightName, checkIn, checkOut")
    void crearReserva_valida_retorna201ConDatos() throws Exception {
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setCheckIn(LocalDate.of(2026, 10, 1));
        req.setCheckOut(LocalDate.of(2026, 10, 7));

        mvc.perform(post(RESERVATIONS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.flightId").value(1))
                .andExpect(jsonPath("$.flightName").isNotEmpty())
                .andExpect(jsonPath("$.checkIn").value("2026-10-01"))
                .andExpect(jsonPath("$.checkOut").value("2026-10-07"))
                .andExpect(jsonPath("$.userEmail").value("user@stellarjets.com"));
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#32 — Realizar reserva")
    @Story("Respuesta de reserva incluye datos de origen y destino del vuelo")
    @DisplayName("TC-S4-12 | US#32 — Reserva: respuesta incluye flightOrigin y flightDestination")
    void crearReserva_respuestaIncluyeRuta() throws Exception {
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setCheckIn(LocalDate.of(2026, 11, 1));
        req.setCheckOut(LocalDate.of(2026, 11, 5));

        mvc.perform(post(RESERVATIONS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightOrigin").isNotEmpty())
                .andExpect(jsonPath("$.flightDestination").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#32 — Realizar reserva")
    @Story("Reserva sobre vuelo inexistente devuelve 404")
    @DisplayName("TC-S4-13 | US#32 — Reserva en vuelo inexistente: devuelve 404 Not Found")
    void crearReserva_vueloInexistente_retorna404() throws Exception {
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setCheckIn(LocalDate.of(2026, 12, 1));
        req.setCheckOut(LocalDate.of(2026, 12, 5));

        mvc.perform(post(RESERVATIONS + "/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#32 — Realizar reserva")
    @Story("Reserva sin body devuelve 400")
    @DisplayName("TC-S4-14 | US#32 — Reserva sin body: devuelve 400 Bad Request")
    void crearReserva_sinBody_retorna400() throws Exception {
        mvc.perform(post(RESERVATIONS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ─── US#33: Historial de reservas ─────────────────────────────────────────

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#33 — Historial de reservas")
    @Story("Historial vacío devuelve array vacío para usuario sin reservas")
    @DisplayName("TC-S4-15 | US#33 — Historial sin reservas: devuelve array vacío")
    void historial_sinReservas_retornaVacio() throws Exception {
        mvc.perform(get(RESERVATIONS + "/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#33 — Historial de reservas")
    @Story("Reserva creada aparece en el historial del usuario")
    @DisplayName("TC-S4-16 | US#33 — Reserva aparece en historial tras crearla")
    void historial_conReserva_aparece() throws Exception {
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setCheckIn(LocalDate.of(2027, 1, 10));
        req.setCheckOut(LocalDate.of(2027, 1, 15));

        mvc.perform(post(RESERVATIONS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mvc.perform(get(RESERVATIONS + "/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].flightName").isNotEmpty())
                .andExpect(jsonPath("$[0].checkIn").isNotEmpty())
                .andExpect(jsonPath("$[0].checkOut").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#33 — Historial de reservas")
    @Story("Historial incluye campos de ruta y creación de la reserva")
    @DisplayName("TC-S4-17 | US#33 — Historial: cada reserva incluye flightOrigin, flightDestination y createdAt")
    void historial_incluyeCamposCompletos() throws Exception {
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setCheckIn(LocalDate.of(2027, 2, 1));
        req.setCheckOut(LocalDate.of(2027, 2, 7));

        mvc.perform(post(RESERVATIONS + "/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mvc.perform(get(RESERVATIONS + "/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightOrigin").isNotEmpty())
                .andExpect(jsonPath("$[0].flightDestination").isNotEmpty())
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].userEmail").value("user@stellarjets.com"));
    }

    @Test
    @Feature("US#33 — Historial de reservas")
    @Story("Historial sin autenticación es rechazado")
    @DisplayName("TC-S4-18 | US#33 — Historial sin autenticación: devuelve 403 Forbidden")
    void historial_sinAuth_esRechazado() throws Exception {
        mvc.perform(get(RESERVATIONS + "/my"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@stellarjets.com")
    @Feature("US#33 — Historial de reservas")
    @Story("Múltiples reservas se devuelven ordenadas con la más reciente primero")
    @DisplayName("TC-S4-19 | US#33 — Múltiples reservas: devuelve lista ordenada (más reciente primero)")
    void historial_multipleReservas_ordenPorFecha() throws Exception {
        ReservationRequestDTO r1 = new ReservationRequestDTO();
        r1.setCheckIn(LocalDate.of(2027, 3, 1));
        r1.setCheckOut(LocalDate.of(2027, 3, 5));

        ReservationRequestDTO r2 = new ReservationRequestDTO();
        r2.setCheckIn(LocalDate.of(2027, 4, 1));
        r2.setCheckOut(LocalDate.of(2027, 4, 5));

        mvc.perform(post(RESERVATIONS + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(r1)))
                .andExpect(status().isCreated());

        mvc.perform(post(RESERVATIONS + "/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(r2)))
                .andExpect(status().isCreated());

        mvc.perform(get(RESERVATIONS + "/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[1].id").isNumber());
    }
}
