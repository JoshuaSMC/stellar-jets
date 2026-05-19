# ✈️ Stellar Jets

> *Donde las estrellas guían tu destino*

**Desafío Profesional · Digital House · Sprint 1 · 2026**

---

## 📋 Entregable 01 — Documentación / Bitácora

### Definición del proyecto

**Stellar Jets** es una plataforma web de reservas de vuelos premium orientada a usuarios que buscan experiencias de viaje de alto nivel. La aplicación permite explorar destinos, visualizar detalles de cada vuelo con galería de imágenes, filtrar por categoría y realizar reservas. Un panel de administración permite gestionar el catálogo completo de productos.

**Problema que resuelve:** centralizar la búsqueda y reserva de vuelos en una interfaz moderna, rápida y visualmente premium, inspirada en el estándar de aerolíneas de lujo como Qatar Airways.

**Usuarios:**
- **Usuario final:** explora vuelos, filtra por categoría, ve detalles y reserva.
- **Administrador:** gestiona el catálogo (crear, editar, activar/desactivar, eliminar vuelos).

**Alcance Sprint 1:** estructura base del sitio, registro y visualización de productos, eliminación, paginación, galería de imágenes y panel de administración.

### Rol: Scrum Master

| Ítem | Detalle |
|------|---------|
| **Metodología** | Scrum — iteraciones por sprint |
| **Sprint** | Sprint 1 |
| **Meta del sprint** | Estructura base + CRUD de productos + visualización |
| **Equipo** | Full-stack (Frontend · Backend · BBDD · Infra · UX/UI · QA) |
| **Herramientas** | GitHub (repositorio) · Maven (build) · Vite (dev server) |

**Historias de usuario completadas:** US#1 · US#2 · US#3 · US#4 · US#5 · US#6 · US#7 · US#8 · US#9 · US#10 · US#11 ✅

---

## 🎨 Entregable 02 — Identidad de Marca

### Logo

```
  ✈  STELLAR JETS
     PREMIUM AVIATION
```

- **Tipografía:** Cinzel 700 — serif romana uppercase (evoca lujo, elegancia atemporal)
- **Color:** Dorado `#D4AF37` sobre fondo oscuro navy `#0A1428`
- **Subtítulo:** "PREMIUM AVIATION" en Cinzel 400, tracking extendido, blanco/35%
- **Slogan:** *Donde las estrellas guían tu destino*

### Paleta de colores

| Rol | Nombre | Hex | Uso |
|-----|--------|-----|-----|
| ![#0A1428](https://placehold.co/16x16/0A1428/0A1428.png) | **Navy Principal** | `#0A1428` | Header, footer, fondos oscuros |
| ![#060E1A](https://placehold.co/16x16/060E1A/060E1A.png) | **Navy Profundo** | `#060E1A` | Footer background |
| ![#D4AF37](https://placehold.co/16x16/D4AF37/D4AF37.png) | **Dorado Principal** | `#D4AF37` | Logo, botones primarios, acentos |
| ![#F5D576](https://placehold.co/16x16/F5D576/F5D576.png) | **Dorado Claro** | `#F5D576` | Hover states, highlights |
| ![#F4F7FB](https://placehold.co/16x16/F4F7FB/F4F7FB.png) | **Fondo Claro** | `#F4F7FB` | Background principal del sitio |
| ![#FFFFFF](https://placehold.co/16x16/FFFFFF/FFFFFF.png) | **Blanco** | `#FFFFFF` | Texto sobre fondos oscuros |
| ![#64748B](https://placehold.co/16x16/64748B/64748B.png) | **Gris Texto** | `#64748B` | Textos secundarios sobre fondo claro |

### Tipografías

| Rol | Familia | Peso | Uso |
|-----|---------|------|-----|
| **Display** | Cinzel | 700 | Logo, titulares premium |
| **Cuerpo** | Inter | 300–700 | Todo el texto de la interfaz |

---

## 🧪 Entregable 03 — Planificación y Ejecución de Tests

### Plan de pruebas — Sprint 1

**Objetivo:** verificar que todas las historias de usuario del Sprint 1 cumplen sus criterios de aceptación.
**Entorno:** `http://localhost:5173` (frontend) · `http://localhost:8080` (backend H2 in-memory).

**Tipos de test:**
- 🤖 **API (JUnit 5 + MockMvc)** — tests de integración contra el backend con H2 in-memory.
- 🌐 **UI (Selenium + WebDriverManager)** — tests end-to-end en Chrome headless. Requieren frontend y backend corriendo.
- 👁 **Manual** — criterios visuales no automatizables.

**Comandos de ejecución:**

```bash
# Solo tests de API (sin servidores):
cd stellar-jets-backend
JAVA_HOME=/opt/homebrew/opt/openjdk@21 mvn test -Dgroups="!ui"

# Tests de UI con Selenium (requiere ambos servidores activos):
cd stellar-jets-frontend && npm run dev &   # puerto 5173
cd stellar-jets-backend && JAVA_HOME=... mvn spring-boot:run &  # puerto 8080
cd stellar-jets-backend && JAVA_HOME=... mvn test -Dgroups=ui

# Todos los tests:
JAVA_HOME=/opt/homebrew/opt/openjdk@21 mvn test
```

**Resultado: 19/19 API tests PASS · 20 UI tests (Selenium)** — `BUILD SUCCESS`

---

### Casos de prueba — ejecución completa

| # | Tipo | Historia | Caso de prueba | Resultado esperado | Estado |
|---|------|----------|---------------|-------------------|--------|
| TC-01 | 🌐 Selenium | US#1 Header | Header visible en la página principal | Elemento `<header>` visible | ✅ PASS |
| TC-02 | 🌐 Selenium | US#1 Header | Logo "STELLAR JETS" presente en el header | Texto encontrado en el header | ✅ PASS |
| TC-03 | 🌐 Selenium | US#1 Header | Botones "Crear cuenta" e "Iniciar sesión" | Ambos textos presentes en el header | ✅ PASS |
| TC-04 | 🌐 Selenium | US#1 Header | Clic en logo redirige al home | URL = `http://localhost:5173/` | ✅ PASS |
| TC-05 | 🌐 Selenium | US#1 Header | Header fijo al hacer scroll | CSS `position: fixed` o `sticky` | ✅ PASS |
| TC-06 | 🌐 Selenium | US#2 Home | Sección buscador visible | Input de búsqueda presente en `<main>` | ✅ PASS |
| TC-07 | 🌐 Selenium | US#2 Home | Sección recomendados visible | Texto "recomend" presente en `<main>` | ✅ PASS |
| TC-08 | 👁 Manual | US#2 Home | Background acorde a identidad de marca | Color `#F4F7FB` visible y consistente | ✅ PASS |
| TC-09 | 👁 Manual | US#2 Home | Responsividad del home | Adapta correctamente a mobile, tablet y desktop | ✅ PASS |
| TC-08 | 🤖 Auto | US#3 Admin | Crear producto con nombre único | HTTP 201 · producto creado con id asignado | ✅ PASS |
| TC-09 | 🤖 Auto | US#3 Admin | Nombre duplicado | HTTP 409 · mensaje "nombre ya en uso" | ✅ PASS |
| TC-10 | 🤖 Auto | US#3 Admin | Código de vuelo duplicado | HTTP 409 · mensaje "código ya en uso" | ✅ PASS |
| TC-11 | 🤖 Auto | US#3 Admin | Subir múltiples imágenes | HTTP 201 · `images` devuelve 3 elementos | ✅ PASS |
| TC-12 | 🤖 Auto | US#4 Home | Paginación máximo 10 productos | `pageSize=10` · `content.length ≤ 10` | ✅ PASS |
| TC-13 | 🤖 Auto | US#4 Home | Búsqueda por nombre filtra correctamente | Solo aparecen productos que coinciden | ✅ PASS |
| TC-14 | 🌐 Selenium | US#4 Home | Productos visibles en el home (máx 10) | Cards de vuelos presentes, cantidad ≤ 10 | ✅ PASS |
| TC-15 | 👁 Manual | US#4 Home | Distribución 2 columnas × 5 filas | Layout correcto en desktop | ✅ PASS |
| TC-16 | 🤖 Auto | US#5 Detalle | API devuelve nombre, descripción e imágenes | `name`, `description` e `images` presentes | ✅ PASS |
| TC-17 | 🌐 Selenium | US#5 Detalle | Página de detalle carga correctamente | `<main>` visible en `/flights/1` | ✅ PASS |
| TC-18 | 🌐 Selenium | US#5 Detalle | Header hero ocupa el ancho completo | `heroWidth ≥ viewportWidth - 20px` | ✅ PASS |
| TC-19 | 🌐 Selenium | US#5 Detalle | Botón "Volver" presente en el detalle | Texto "Volver" encontrado en `<main>` | ✅ PASS |
| TC-20 | 👁 Manual | US#5 Detalle | Título alineado a la izquierda | Título visible en el sector izquierdo | ✅ PASS |
| TC-21 | 🤖 Auto | US#6 Galería | API devuelve 5 imágenes | `images.length = 5` | ✅ PASS |
| TC-22 | 🤖 Auto | US#6 Galería | Primera imagen marcada como cover | `images[0].cover = true` · `images[1].cover = false` | ✅ PASS |
| TC-23 | 🌐 Selenium | US#6 Galería | Imágenes visibles en el detalle | Elementos `<img>` presentes | ✅ PASS |
| TC-24 | 🌐 Selenium | US#6 Galería | Botón "Ver más" presente | Texto "Ver más" encontrado en `<main>` | ✅ PASS |
| TC-25 | 🌐 Selenium | US#6 Galería | Clic en "Ver más" abre lightbox | Elemento con `z-50` / `fixed inset` visible | ✅ PASS |
| TC-26 | 👁 Manual | US#6 Galería | Layout desktop: imagen izq + grid 2×2 der | Layout correcto en pantallas ≥ 640px | ✅ PASS |
| TC-27 | 👁 Manual | US#6 Galería | Galería responsive en mobile/tablet | Layout apilado y funcional | ✅ PASS |
| TC-28 | 🌐 Selenium | US#7 Footer | Footer visible en el home | Elemento `<footer>` visible | ✅ PASS |
| TC-29 | 🌐 Selenium | US#7 Footer | Footer contiene logo, año y copyright | "STELLAR JETS" + "©" / "2026" en footer | ✅ PASS |
| TC-30 | 🌐 Selenium | US#7 Footer | Footer visible en página de detalle | Elemento `<footer>` visible en `/flights/1` | ✅ PASS |
| TC-31 | 👁 Manual | US#7 Footer | Responsividad del footer | Adapta a mobile y tablet | ✅ PASS |
| TC-27 | 🤖 Auto | US#8 Paginación | Metadatos de paginación presentes | `currentPage`, `totalPages`, `totalElements`, `first` | ✅ PASS |
| TC-28 | 🤖 Auto | US#8 Paginación | Paginación con filtro de categoría | Responde 200 con array de productos | ✅ PASS |
| TC-29 | 👁 Manual | US#8 Paginación | Botones « Inicio · ← Anterior · Siguiente → | Todos navegan correctamente | ✅ PASS |
| TC-33 | 🤖 Auto | US#9 Admin | Endpoint admin API accesible | GET `/api/admin/flights` responde 200 | ✅ PASS |
| TC-34 | 🤖 Auto | US#9 Admin | Toggle activo/inactivo | Estado cambia correctamente en la respuesta | ✅ PASS |
| TC-35 | 🤖 Auto | US#9 Admin | Editar producto actualiza datos | Nombre y descripción actualizados en la respuesta | ✅ PASS |
| TC-36 | 🌐 Selenium | US#9 Admin | URL `/administracion` accesible | Panel o mensaje visible en la página | ✅ PASS |
| TC-37 | 🌐 Selenium | US#9 Admin | Botón "Lista de productos" presente | Texto encontrado en el panel | ✅ PASS |
| TC-38 | 🌐 Selenium | US#9 Admin | Botón "Agregar producto" presente | Texto encontrado en el panel | ✅ PASS |
| TC-39 | 👁 Manual | US#9 Admin | Panel no disponible en mobile | Mensaje de aviso en pantallas < 768px | ✅ PASS |
| TC-35 | 🤖 Auto | US#10 Admin | Listado admin paginado con id y nombre | `content[*].id` y `content[*].name` presentes | ✅ PASS |
| TC-36 | 🤖 Auto | US#10 Admin | Producto creado aparece en listado admin | Nombre presente en `content[*].name` | ✅ PASS |
| TC-37 | 👁 Manual | US#10 Admin | Columnas Id, Nombre, Acciones visibles | Tabla con las 3 columnas requeridas | ✅ PASS |
| TC-38 | 🤖 Auto | US#11 Admin | Eliminar producto: desaparece del sistema | DELETE 204 · GET posterior devuelve 404 | ✅ PASS |
| TC-39 | 🤖 Auto | US#11 Admin | Eliminar producto inexistente | HTTP 404 | ✅ PASS |
| TC-40 | 👁 Manual | US#11 Admin | Modal de confirmación al presionar Eliminar | Modal visible con opciones Confirmar / Cancelar | ✅ PASS |
| TC-41 | 👁 Manual | US#11 Admin | Cancelar eliminación | Sin cambios, producto permanece en el listado | ✅ PASS |

**Resultado total: 49/49 casos PASS ✅** (19 API · 20 Selenium · 10 Manuales)

---

## ⚙️ Tecnologías

### 🖥️ Frontend
- React 18 + TypeScript
- Vite 5
- Tailwind CSS 3
- React Router v6
- Axios

### ☕ Backend
- Java 21
- Spring Boot 3.3.5
- Spring Data JPA + Hibernate
- H2 Database (in-memory)
- Lombok 1.18.38
- Maven 3.8+

---

## 🚀 Instalación local

### 🧩 Requisitos previos
- Java 21 — en macOS: `/opt/homebrew/opt/openjdk@21`
- Node.js 18+
- Maven 3.8+

### 📦 Clonar el repositorio

```bash
git clone https://github.com/[usuario]/stellar-jets.git
cd stellar-jets
```

---

### ☕ Backend (`/stellar-jets-backend`)

> La base de datos H2 es **in-memory**: se crea automáticamente al iniciar con 11 vuelos de ejemplo. No se necesita instalar ni configurar ninguna base de datos externa.

```bash
cd stellar-jets-backend
JAVA_HOME=/opt/homebrew/opt/openjdk@21 mvn spring-boot:run
```

El backend estará disponible en `http://localhost:8080`

> **IntelliJ IDEA:** Settings → Maven → Runner → JRE → `/opt/homebrew/opt/openjdk@21`

No se requiere archivo `.env` para el Sprint 1. La configuración está en `src/main/resources/application.properties`.

---

### 🖼️ Frontend (`/stellar-jets-frontend`)

```bash
cd stellar-jets-frontend
npm install
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

No se requiere archivo `.env`. El frontend usa el proxy de Vite para redirigir `/api/*` → `http://localhost:8080`.

---

## 📬 Endpoints de la API

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/flights/search?page=0&size=10` | Vuelos paginados (orden aleatorio sin filtro) | ❌ |
| GET | `/api/flights/search?query=X` | Búsqueda por nombre, ciudad o código IATA | ❌ |
| GET | `/api/flights/search?categoryId=1` | Filtrar por categoría | ❌ |
| GET | `/api/flights/recommended` | Top 10 vuelos por rating | ❌ |
| GET | `/api/flights/{id}` | Detalle de un vuelo | ❌ |
| GET | `/api/admin/flights` | Listado admin paginado | ❌ |
| POST | `/api/admin/flights` | Crear vuelo | ❌ |
| PUT | `/api/admin/flights/{id}` | Editar vuelo | ❌ |
| DELETE | `/api/admin/flights/{id}` | Eliminar vuelo | ❌ |
| PATCH | `/api/admin/flights/{id}/toggle` | Activar / desactivar vuelo | ❌ |
| GET | `/api/categories` | Todas las categorías | ❌ |
| GET | `/api/categories/active` | Categorías con vuelos activos | ❌ |

> Sprint 1 no incluye autenticación — se implementa en Sprint 2.

---

## 🗂️ Diagrama de Entidades

```
┌─────────────────────────────────────────────┐
│                   Flight                    │
├─────────────────────────────────────────────┤
│ id (PK)          bigint                     │
│ flightNumber     varchar                    │
│ name             varchar (único)            │
│ description      text                       │
│ price            decimal                    │
│ availableSeats   int                        │
│ durationMinutes  int                        │
│ rating           double                     │
│ active           boolean                    │
│ origin_city      varchar  ┐ @Embeddable     │
│ origin_country   varchar  │ AirportInfo     │
│ origin_iataCode  varchar  ┘                 │
│ dest_city        varchar  ┐ @Embeddable     │
│ dest_country     varchar  │ AirportInfo     │
│ dest_iataCode    varchar  ┘                 │
│ category_id (FK) bigint                     │
└─────────────────────────────────────────────┘
         │ 1                        │ 1
         │                          │
         ▼ N                        ▼ N
┌─────────────────┐     ┌─────────────────────┐
│    Category     │     │     FlightImage      │
├─────────────────┤     ├─────────────────────┤
│ id (PK)         │     │ id (PK)             │
│ name            │     │ url                 │
│ description     │     │ altText             │
└─────────────────┘     │ cover   boolean     │
                        │ flight_id (FK)      │
                        └─────────────────────┘
```

---

## ✅ Checklist Sprint 1

### US#1 · Header
- [x] Ocupa el 100% del ancho en todas las páginas
- [x] Fijo en la parte superior al hacer scroll
- [x] Consistente en todas las páginas
- [x] Responsive — menú hamburguesa en mobile
- [x] Logo y slogan alineados a la izquierda
- [x] Clic en logo redirige al home
- [x] Botones "Crear cuenta" e "Iniciar sesión" a la derecha (sin funcionalidad — Sprint 2)

### US#2 · Cuerpo del sitio
- [x] Background `#F4F7FB` coherente con identidad de marca
- [x] Ocupa el 100% del alto de la pantalla
- [x] Responsive para diferentes dispositivos
- [x] Tres secciones visibles: buscador, categorías y recomendaciones

### US#3 · Registrar producto
- [x] Botón "Agregar producto" en el panel admin
- [x] Formulario con nombre, descripción e imágenes
- [x] Soporte para una o más imágenes (URLs)
- [x] Producto guardado correctamente en la base de datos
- [x] Producto aparece en el listado y en el home
- [x] Error si se intenta agregar un nombre duplicado

### US#4 · Visualizar productos en home
- [x] Máximo 10 productos por página
- [x] Sin productos repetidos
- [x] Distribuidos en 2 columnas × máximo 5 filas
- [x] Orden verdaderamente aleatorio (`ORDER BY RAND()` — H2)

### US#5 · Detalle de producto
- [x] Header hero al 100% del ancho con imagen del vuelo
- [x] Título alineado a la izquierda
- [x] Flecha "← Volver" alineada a la derecha
- [x] Body con texto descriptivo e imágenes del producto

### US#6 · Galería de imágenes
- [x] Bloque al 100% del ancho con 5 imágenes visibles
- [x] Imagen principal en la mitad izquierda
- [x] Grid 2 filas × 2 columnas en la mitad derecha
- [x] Botón "Ver más (N)" en la esquina inferior derecha
- [x] Lightbox con navegación entre todas las imágenes
- [x] Responsive en mobile y tablet

### US#7 · Footer
- [x] Ocupa el 100% del ancho en todas las páginas
- [x] Isologotipo, año y copyright alineados a la izquierda
- [x] Responsive

### US#8 · Paginación
- [x] Máximo 10 productos por página
- [x] Contador de páginas funcional
- [x] Botones: « Inicio · ← Anterior · Siguiente →
- [x] Funciona con búsqueda y filtros de categoría

### US#9 · Panel de administración
- [x] URL `/administracion` para acceder al panel
- [x] Menú con todas las funciones desarrolladas
- [x] No responsive — mensaje en mobile: "Panel no disponible en dispositivos móviles"

### US#10 · Listar productos
- [x] Botón "Lista de productos" en el panel admin
- [x] Lista todos los productos disponibles
- [x] Columnas: ID, Nombre, Acciones (+ Ruta, Categoría, Precio, Estado)

### US#11 · Eliminar producto
- [x] Acción "Eliminar" por producto en el listado admin
- [x] Modal de confirmación antes de eliminar
- [x] Confirmación: elimina de BD y desaparece del listado
- [x] Cancelación: sin cambios

---

## 🏗️ Estructura del proyecto

```
stellar-jets/
├── stellar-jets-backend/
│   └── src/main/java/com/stellarjets/
│       ├── controller/     FlightController · CategoryController
│       ├── service/        FlightService · CategoryService
│       ├── entity/         Flight · Category · FlightImage · AirportInfo
│       ├── dto/            FlightDTO · FlightRequestDTO · AirportDTO · PagedResponseDTO
│       ├── repository/     FlightRepository · CategoryRepository
│       └── config/         DataInitializer (11 vuelos de ejemplo al iniciar)
│
└── stellar-jets-frontend/
    └── src/
        ├── components/     Header · Footer · FlightCard · ImageGallery · Pagination
        ├── pages/          HomePage · FlightDetailPage · AdminPage
        ├── api/            flightApi.ts
        └── types/          index.ts
```

---

## 👤 Autor

- [@JoshuaSMC](https://github.com/JoshuaSMC) — Roles: TL Frontend · TL Backend · TL BBDD · Scrum Master · UX/UI

---

## 📄 Licencia

MIT

---

*Desarrollado para Digital House — Desafío Profesional · Sprint 1 · 2026*
