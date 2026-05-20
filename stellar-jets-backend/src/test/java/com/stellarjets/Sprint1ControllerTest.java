package com.stellarjets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellarjets.dto.AirportRequestDTO;
import com.stellarjets.dto.FlightRequestDTO;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Epic("Stellar Jets — Backend")
@DisplayName("Sprint 1 — Tests de integración")
class Sprint1ControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private static final String ADMIN  = "/api/admin/flights";
    private static final String PUBLIC = "/api/flights";

    private FlightRequestDTO buildFlight(String name, String flightNumber) {
        AirportRequestDTO origen  = AirportRequestDTO.builder().city("Buenos Aires").country("Argentina").iataCode("EZE").build();
        AirportRequestDTO destino = AirportRequestDTO.builder().city("Madrid").country("España").iataCode("MAD").build();
        return FlightRequestDTO.builder()
                .name(name)
                .flightNumber(flightNumber)
                .description("Vuelo de prueba")
                .price(new BigDecimal("150000"))
                .availableSeats(100)
                .durationMinutes(720)
                .rating(new BigDecimal("4.5"))
                .origin(origen)
                .destination(destino)
                .imageUrls(List.of("https://example.com/img1.jpg"))
                .build();
    }

    // ─── US#3: Registrar producto ────────────────────────────────────────────

    @Test
    @Feature("US#3 — Registrar producto")
    @Story("Crear producto válido")
    @DisplayName("TC-01 | US#3 — Crear producto: se guarda y aparece en el listado")
    void crearProducto_guardaYApareceEnListado() throws Exception {
        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Madrid Express", "SJ-T01"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Vuelo Madrid Express"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @Feature("US#3 — Registrar producto")
    @Story("Nombre duplicado retorna conflicto")
    @DisplayName("TC-02 | US#3 — Nombre duplicado: responde 409 con mensaje de error")
    void crearProducto_nombreDuplicado_retorna409() throws Exception {
        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Tokio Business", "SJ-T02"))))
                .andExpect(status().isCreated());

        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Tokio Business", "SJ-T99"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("nombre")));
    }

    @Test
    @Feature("US#3 — Registrar producto")
    @Story("Código de vuelo duplicado retorna conflicto")
    @DisplayName("TC-03 | US#3 — Código de vuelo duplicado: responde 409 con mensaje de error")
    void crearProducto_codigoDuplicado_retorna409() throws Exception {
        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo París Premium", "SJ-T10"))))
                .andExpect(status().isCreated());

        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo París Express", "SJ-T10"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("código")));
    }

    @Test
    @Feature("US#3 — Registrar producto")
    @Story("Múltiples imágenes se guardan correctamente")
    @DisplayName("TC-04 | US#3 — Producto con múltiples imágenes se guarda correctamente")
    void crearProducto_multipleImagenes_guardaTodas() throws Exception {
        FlightRequestDTO dto = buildFlight("Vuelo Roma Coliseo", "SJ-T20");
        dto.setImageUrls(List.of(
                "https://example.com/img1.jpg",
                "https://example.com/img2.jpg",
                "https://example.com/img3.jpg"
        ));

        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.images", hasSize(3)));
    }

    // ─── US#4: Visualizar productos en el home ────────────────────────────────

    @Test
    @Feature("US#4 — Visualizar productos en el home")
    @Story("Listado paginado máximo 10 productos")
    @DisplayName("TC-05 | US#4 — Home muestra máximo 10 productos por página")
    void home_maximos10Productos() throws Exception {
        mvc.perform(get(PUBLIC + "/search").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))));
    }

    @Test
    @Feature("US#4 — Visualizar productos en el home")
    @Story("Búsqueda por nombre filtra resultados")
    @DisplayName("TC-06 | US#4 — Búsqueda por nombre filtra correctamente")
    void home_busquedaPorNombre_filtra() throws Exception {
        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Exclusivo Bali", "SJ-T30"))))
                .andExpect(status().isCreated());

        mvc.perform(get(PUBLIC + "/search").param("query", "Bali").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(containsString("Bali")));
    }

    // ─── US#5: Detalle de producto ────────────────────────────────────────────

    @Test
    @Feature("US#5 — Detalle de producto")
    @Story("Detalle devuelve nombre, descripción e imágenes")
    @DisplayName("TC-07 | US#5 — Detalle de producto devuelve nombre, descripción e imágenes")
    void detalle_devuelveInfoCompleta() throws Exception {
        String resp = mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Detalle Test", "SJ-T40"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        mvc.perform(get(PUBLIC + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vuelo Detalle Test"))
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.images", hasSize(greaterThanOrEqualTo(1))));
    }

    // ─── US#10: Listar productos ──────────────────────────────────────────────

    @Test
    @Feature("US#10 — Listar productos (admin)")
    @Story("Listado admin devuelve paginación")
    @DisplayName("TC-08 | US#10 — Listado admin devuelve id, nombre y productos con paginación")
    void admin_listarProductos_devuelvePaginado() throws Exception {
        mvc.perform(get(ADMIN).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    @Feature("US#10 — Listar productos (admin)")
    @Story("Producto creado aparece en listado con su id")
    @DisplayName("TC-09 | US#10 — Producto creado aparece en el listado admin con su id")
    void admin_productoCreado_apareceEnListado() throws Exception {
        mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Lista Admin", "SJ-T50"))))
                .andExpect(status().isCreated());

        mvc.perform(get(ADMIN).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name", hasItem("Vuelo Lista Admin")))
                .andExpect(jsonPath("$.content[*].id").isNotEmpty());
    }

    // ─── US#11: Eliminar producto ─────────────────────────────────────────────

    @Test
    @Feature("US#11 — Eliminar producto")
    @Story("Eliminar producto lo quita del listado")
    @DisplayName("TC-10 | US#11 — Eliminar producto: responde 204 y desaparece del listado")
    void eliminarProducto_desapareceDelListado() throws Exception {
        String resp = mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Para Eliminar", "SJ-T60"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        mvc.perform(delete(ADMIN + "/" + id))
                .andExpect(status().isNoContent());

        mvc.perform(get(PUBLIC + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Feature("US#11 — Eliminar producto")
    @Story("Eliminar producto inexistente retorna 404")
    @DisplayName("TC-11 | US#11 — Eliminar producto inexistente: responde 404")
    void eliminarProductoInexistente_retorna404() throws Exception {
        mvc.perform(delete(ADMIN + "/99999"))
                .andExpect(status().isNotFound());
    }

    // ─── US#8: Paginación ────────────────────────────────────────────────────

    @Test
    @Feature("US#8 — Paginación")
    @Story("Respuesta incluye metadatos de paginación")
    @DisplayName("TC-12 | US#8 — Paginación: la respuesta incluye currentPage, totalPages y totalElements")
    void paginacion_incluyeMetadatos() throws Exception {
        mvc.perform(get(PUBLIC + "/search").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    @Feature("US#8 — Paginación")
    @Story("Filtro por categoría con paginación funciona")
    @DisplayName("TC-13 | US#8 — Paginación con filtro de categoría funciona correctamente")
    void paginacion_conFiltroCategoria_funciona() throws Exception {
        mvc.perform(get(PUBLIC + "/search").param("categoryIds", "1").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content").isArray());
    }

    // ─── US#6: Galería de imágenes ───────────────────────────────────────────

    @Test
    @Feature("US#6 — Galería de imágenes")
    @Story("API devuelve array de imágenes con URL y flag cover")
    @DisplayName("TC-14 | US#6 — API devuelve array de imágenes para el detalle del producto")
    void galeria_apiDevuelveImagenes() throws Exception {
        FlightRequestDTO dto = buildFlight("Vuelo Galería Test", "SJ-T70");
        dto.setImageUrls(List.of(
                "https://example.com/img1.jpg",
                "https://example.com/img2.jpg",
                "https://example.com/img3.jpg",
                "https://example.com/img4.jpg",
                "https://example.com/img5.jpg"
        ));

        String resp = mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        mvc.perform(get(PUBLIC + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images", hasSize(5)))
                .andExpect(jsonPath("$.images[0].cover").value(true))
                .andExpect(jsonPath("$.images[0].url").isNotEmpty());
    }

    @Test
    @Feature("US#6 — Galería de imágenes")
    @Story("Primera imagen marcada como cover")
    @DisplayName("TC-15 | US#6 — Primera imagen marcada como cover (imagen principal)")
    void galeria_primeraImagenEsCover() throws Exception {
        FlightRequestDTO dto = buildFlight("Vuelo Cover Test", "SJ-T71");
        dto.setImageUrls(List.of("https://example.com/cover.jpg", "https://example.com/extra.jpg"));

        String resp = mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        mvc.perform(get(PUBLIC + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images[0].cover").value(true))
                .andExpect(jsonPath("$.images[1].cover").value(false));
    }

    // ─── US#9: Panel de administración ───────────────────────────────────────

    @Test
    @Feature("US#9 — Panel de administración")
    @Story("Endpoint admin responde 200")
    @DisplayName("TC-16 | US#9 — Endpoint admin /api/admin/flights responde 200")
    void adminPanel_endpointAccesible() throws Exception {
        mvc.perform(get(ADMIN).param("page", "0").param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @Feature("US#9 — Panel de administración")
    @Story("Toggle activo/inactivo cambia estado")
    @DisplayName("TC-17 | US#9 — Toggle activo/inactivo cambia el estado del producto")
    void adminPanel_toggle_cambiaEstado() throws Exception {
        String resp = mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Toggle Test", "SJ-T80"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();
        boolean estadoInicial = mapper.readTree(resp).get("active").asBoolean();

        mvc.perform(patch(ADMIN + "/" + id + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(!estadoInicial));
    }

    @Test
    @Feature("US#9 — Panel de administración")
    @Story("Editar producto actualiza los datos")
    @DisplayName("TC-18 | US#9 — Editar producto actualiza los datos correctamente")
    void adminPanel_editar_actualizaDatos() throws Exception {
        String resp = mvc.perform(post(ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buildFlight("Vuelo Original", "SJ-T90"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();

        FlightRequestDTO actualizado = buildFlight("Vuelo Actualizado", "SJ-T90");
        actualizado.setDescription("Descripción actualizada");

        mvc.perform(put(ADMIN + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vuelo Actualizado"))
                .andExpect(jsonPath("$.description").value("Descripción actualizada"));
    }
}
