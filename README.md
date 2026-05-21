# ✈️ Stellar Jets

Plataforma web de reservas de vuelos premium. Permite a los usuarios registrarse, explorar destinos, visualizar galerías de imágenes, filtrar por categoría y realizar reservas. Los administradores gestionan el catálogo completo de vuelos, categorías y características desde un panel dedicado.

> *Donde las estrellas guían tu destino* · Desafío Profesional · Digital House · 2026

---

## ⚙️ Tecnologías

### 🖥️ Frontend
- React 18 + TypeScript
- Vite 5
- Tailwind CSS 3
- React Router v6
- Axios
- Lucide React (iconografía)

### ☕ Backend
- Java 21
- Spring Boot 3.3.5
- Spring Security 6 + JWT (JJWT 0.12)
- Spring Data JPA + Hibernate
- H2 Database (in-memory)
- Spring Mail (Mailtrap sandbox)
- Lombok 1.18.38
- Maven 3.8+

### 🧪 Testing
- JUnit 5 + MockMvc (tests de integración API)
- Selenium 4 + WebDriverManager (tests UI end-to-end)
- Allure (reportes visuales)
- Page Object Model

---

## 🚀 Instalación local

### 🧩 Requisitos previos
- Java 21+
- Node.js 18+
- Maven 3.8+

### 📦 Clonar el repositorio
```bash
git clone https://github.com/[usuario]/stellar-jets.git
cd stellar-jets
```

---

### ☕ Backend (`/stellar-jets-backend`)

> La base de datos H2 es **in-memory**: se crea automáticamente al iniciar con 11 vuelos, 4 categorías, 8 características y 2 usuarios de prueba. No requiere instalar ni configurar ninguna base de datos.

#### Variables de entorno (opcionales — solo para notificación por email):
```bash
# Solo necesarias si querés probar el envío de emails con Mailtrap
MAIL_USERNAME=<tu_mailtrap_user>
MAIL_PASSWORD=<tu_mailtrap_pass>
```

#### Correr el backend:
```bash
cd stellar-jets-backend

# Sin email:
mvn spring-boot:run

# Con notificación por email (Mailtrap):
MAIL_USERNAME=<user> MAIL_PASSWORD=<pass> mvn spring-boot:run
```

> El backend estará disponible en `http://localhost:8080`  
> Swagger UI disponible en `http://localhost:8080/swagger-ui.html`

---

### 🖼️ Frontend (`/stellar-jets-frontend`)

```bash
cd stellar-jets-frontend
npm install
npm run dev
```

> La aplicación estará disponible en `http://localhost:5173`

No se requiere archivo `.env`. El frontend usa el proxy de Vite para redirigir `/api/*` → `http://localhost:8080`.

---

## 📬 Endpoints (API REST)

> Swagger Docs disponible en: `http://localhost:8080/swagger-ui.html`

### Públicos

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Registro de usuario → JWT | ❌ |
| POST | `/api/auth/login` | Login → JWT | ❌ |
| POST | `/api/auth/resend-confirmation` | Reenviar email de confirmación | ❌ |
| GET | `/api/flights/search` | Vuelos paginados con búsqueda y filtro por categoría | ❌ |
| GET | `/api/flights/recommended` | Top 10 vuelos por rating | ❌ |
| GET | `/api/flights/{id}` | Detalle de vuelo con características | ❌ |
| GET | `/api/categories` | Listado de categorías | ❌ |
| GET | `/api/characteristics` | Listado de características | ❌ |

### Protegidos — Rol ADMIN

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET / POST | `/api/admin/flights` | Listar / crear vuelo | ✅ ADMIN |
| PUT / DELETE | `/api/admin/flights/{id}` | Editar / eliminar vuelo | ✅ ADMIN |
| PATCH | `/api/admin/flights/{id}/toggle` | Activar / desactivar vuelo | ✅ ADMIN |
| POST / PUT / DELETE | `/api/admin/categories/{id?}` | CRUD categorías | ✅ ADMIN |
| POST / PUT / DELETE | `/api/admin/characteristics/{id?}` | CRUD características | ✅ ADMIN |
| GET | `/api/admin/users` | Listar usuarios | ✅ ADMIN |
| PATCH | `/api/admin/users/{id}/toggle-role` | Cambiar rol USER ↔ ADMIN | ✅ ADMIN |

---

## 📖 Documentación de la API (Swagger / OpenAPI)

La API está documentada con **springdoc-openapi 2.6.0** (estándar para Spring Boot 3.x en 2026).

Una vez levantado el backend, accedé a:

```
http://localhost:8080/swagger-ui.html
```

Desde Swagger UI podés:
- Ver todos los endpoints con sus parámetros y respuestas
- Probar endpoints públicos directamente desde el navegador
- Autenticarte con JWT para probar endpoints protegidos:
  1. Ejecutar `POST /api/auth/login` con las credenciales de admin
  2. Copiar el token de la respuesta
  3. Hacer clic en el botón **Authorize** 🔒 (arriba a la derecha)
  4. Pegar el token y confirmar
  5. Todos los endpoints admin quedan habilitados en la sesión

---

## 🗂️ Diagrama de Entidades

```
┌─────────────────────────────────────────┐       ┌──────────────────────┐
│                 Flight                  │       │        User          │
├─────────────────────────────────────────┤       ├──────────────────────┤
│ id            bigint (PK)               │       │ id (PK)              │
│ flightNumber  varchar                   │       │ firstName            │
│ name          varchar (único)           │       │ lastName             │
│ description   text                      │       │ email (único)        │
│ price         decimal                   │       │ password (BCrypt)    │
│ rating        double                    │       │ role  USER / ADMIN   │
│ active        boolean                   │       └──────────────────────┘
│ origin_*      @Embeddable AirportInfo   │
│ dest_*        @Embeddable AirportInfo   │
│ category_id   bigint (FK)              │
└─────────────────────────────────────────┘
       │ 1              │ 1                    │ N
       ▼ N              ▼ N                    ▼ N
┌──────────┐    ┌──────────────┐    ┌──────────────────────┐
│ Category │    │ FlightImage  │    │    Characteristic    │
├──────────┤    ├──────────────┤    ├──────────────────────┤
│ id (PK)  │    │ id (PK)      │    │ id (PK)              │
│ name     │    │ url          │    │ name                 │
│ imageUrl │    │ cover bool   │    │ iconName             │
└──────────┘    └──────────────┘    └──────────────────────┘
```

---

## 🧪 Testing

### Estrategia

| Tipo | Herramienta | Requiere servidores |
|------|-------------|---------------------|
| 🤖 API / Integración | JUnit 5 + MockMvc + H2 | No — solo Maven |
| 🌐 UI / End-to-end | Selenium + WebDriverManager | Sí — backend + frontend |
| 👁 Manual | Navegador | Sí |

### Archivos de test

| Archivo | Tipo | Tests |
|---------|------|-------|
| `Sprint1ControllerTest.java` | API | 18 — US#3 al US#11 |
| `Sprint2ControllerTest.java` | API | 28 — US#12 al US#21 |
| `Sprint3ControllerTest.java` | API | 26 — US#22 al US#29 |
| `Sprint1UITest.java` | UI Selenium | 20 — US#1 al US#11 |
| `Sprint2UITest.java` | UI Selenium | 22 — US#12 al US#21 |
| `Sprint3UITest.java` | UI Selenium | 23 — US#22 al US#29 |

**Page Object Model** — cada página tiene su clase en `ui/pages/`:
`BasePage` · `HomePage` · `LoginPage` · `RegisterPage` · `FlightDetailPage` · `AdminPanelPage`

**Usuarios de prueba** (creados automáticamente):

| Email | Password | Rol |
|-------|----------|-----|
| `admin@stellarjets.com` | `admin123` | ADMIN |
| `user@stellarjets.com` | `user123` | USER |

### Cómo ejecutar

**Tests de API** (sin servidores):
```bash
cd stellar-jets-backend
mvn test -Dtest="Sprint1ControllerTest,Sprint2ControllerTest,Sprint3ControllerTest"
```

**Tests de UI con Selenium** (3 terminales):

> Los tests de UI se ejecutan por separado por sprint para garantizar el aislamiento entre suites y evitar interferencias de carga en el backend.

```bash
# Terminal 1 — backend:
cd stellar-jets-backend
MAIL_USERNAME=test MAIL_PASSWORD=test mvn spring-boot:run

# Terminal 2 — frontend:
cd stellar-jets-frontend
npm run dev

# Terminal 3 — tests UI (ejecutar uno por vez):
cd stellar-jets-backend
mvn test -Dtest="Sprint1UITest"
mvn test -Dtest="Sprint2UITest"
mvn test -Dtest="Sprint3UITest"
```

**Todos los tests:**
```bash
cd stellar-jets-backend
mvn test
```

**Reporte visual Allure:**
```bash
cd stellar-jets-backend
mvn allure:serve -Dallure.serve.port=5050
# Abre automáticamente http://localhost:5050
```

> No abrir `index.html` directamente con `file://` — el reporte queda en "loading". Usar siempre `allure:serve`.

### Resultados

| Suite | API | UI Selenium | Manual | Total |
|-------|-----|-------------|--------|-------|
| Sprint 1 | 18 ✅ | 20 ✅ | 12 ✅ | **50 / 50** |
| Sprint 2 | 28 ✅ | 22 ✅ | 12 ✅ | **62 / 62** |
| Sprint 3 | 26 ✅ | 23 ✅ | 16 ✅ | **65 / 65** |

---

## 📋 Entregables del curso

### Sprint 1

#### Entregable 01 — Documentación / Bitácora

**Definición del proyecto:**
**Stellar Jets** es una plataforma web de reservas de vuelos premium. Centraliza la búsqueda y reserva de vuelos en una interfaz moderna, inspirada en aerolíneas de lujo. Los usuarios exploran destinos, visualizan galerías de imágenes, filtran por categoría y realizan reservas. Los administradores gestionan el catálogo completo desde un panel dedicado.

**Rol: Scrum Master**

| Ítem | Detalle |
|------|---------|
| **Metodología** | Scrum — iteraciones por sprint |
| **Sprint** | Sprint 1 |
| **Meta del sprint** | Header · Home · Detalle de producto · Galería · Panel admin · Paginación |
| **Equipo** | Full-stack (Frontend · Backend · BBDD · Infra · UX/UI · QA) |
| **Herramientas** | GitHub · Maven · Vite |

**Bitácora:**

| Iteración | Actividad | Estado |
|-----------|-----------|--------|
| 1 | Estructura base (Spring Boot + React + Vite) | ✅ |
| 2 | Header fijo con logo y botones de navegación | ✅ |
| 3 | Home page con buscador, categorías y recomendados | ✅ |
| 4 | Entidad `Flight` con `AirportInfo` y `FlightImage` | ✅ |
| 5 | CRUD de vuelos desde el panel admin | ✅ |
| 6 | Página de detalle con header hero | ✅ |
| 7 | Galería de imágenes con lightbox | ✅ |
| 8 | Footer con isologotipo y copyright | ✅ |
| 9 | Paginación con metadatos | ✅ |
| 10 | Búsqueda por nombre, ciudad o código IATA | ✅ |
| 11 | Toggle activo/inactivo y eliminación de vuelos | ✅ |

**US completadas:** US#1 · US#2 · US#3 · US#4 · US#5 · US#6 · US#7 · US#8 · US#9 · US#10 · US#11 ✅

#### Entregable 02 — Identidad de Marca

**Logo:**
```
     STELLAR JETS
     PREMIUM AVIATION
```
- Tipografía: Cinzel 700 — serif romana uppercase
- Color: Dorado `#D4AF37` sobre navy `#0A1428`
- Slogan: *Donde las estrellas guían tu destino*

**Paleta de colores:**

| Nombre | Hex | Uso |
|--------|-----|-----|
| Navy Principal | `#0A1428` | Header, footer, fondos oscuros |
| Navy Profundo | `#060E1A` | Footer background |
| Dorado Principal | `#D4AF37` | Logo, botones primarios, acentos |
| Dorado Claro | `#F5D576` | Hover states |
| Fondo Claro | `#F4F7FB` | Background del sitio |
| Blanco | `#FFFFFF` | Texto sobre fondos oscuros |
| Gris Texto | `#64748B` | Textos secundarios |

**Tipografías:**

| Rol | Familia | Peso |
|-----|---------|------|
| Display | Cinzel | 700 |
| Cuerpo | Inter | 300–700 |

#### Entregable 03 — Planificación y Ejecución de Tests

Ver sección [🧪 Testing](#-testing) para la estrategia, comandos de ejecución y resultados.

**Casos de prueba Sprint 1:**

| # | Tipo | US | Caso de prueba | Estado |
|---|------|----|---------------|--------|
| TC-01 | 🌐 Selenium | US#1 | Header visible en la página principal | ✅ |
| TC-02 | 🌐 Selenium | US#1 | Logo "STELLAR JETS" presente en el header | ✅ |
| TC-03 | 🌐 Selenium | US#1 | Botones "Crear cuenta" e "Iniciar sesión" | ✅ |
| TC-04 | 🌐 Selenium | US#1 | Clic en logo redirige al home | ✅ |
| TC-05 | 🌐 Selenium | US#1 | Header fijo al hacer scroll | ✅ |
| TC-06 | 🌐 Selenium | US#2 | Sección buscador visible en el home | ✅ |
| TC-07 | 🌐 Selenium | US#2 | Sección recomendados visible | ✅ |
| TC-08 | 👁 Manual | US#2 | Background acorde a identidad de marca | ✅ |
| TC-09 | 👁 Manual | US#2 | Responsividad del home | ✅ |
| TC-10 | 🤖 API | US#3 | Crear producto con nombre único → HTTP 201 | ✅ |
| TC-11 | 🤖 API | US#3 | Nombre duplicado → HTTP 409 | ✅ |
| TC-12 | 🤖 API | US#3 | Código de vuelo duplicado → HTTP 409 | ✅ |
| TC-13 | 🤖 API | US#3 | Subir múltiples imágenes → array de 3 | ✅ |
| TC-14 | 🤖 API | US#4 | Paginación máximo 10 productos | ✅ |
| TC-15 | 🤖 API | US#4 | Búsqueda por nombre filtra correctamente | ✅ |
| TC-16 | 🌐 Selenium | US#4 | Productos visibles en el home | ✅ |
| TC-17 | 👁 Manual | US#4 | Distribución 2 columnas × 5 filas | ✅ |
| TC-18 | 🤖 API | US#5 | API devuelve nombre, descripción e imágenes | ✅ |
| TC-19 | 🌐 Selenium | US#5 | Página de detalle carga correctamente | ✅ |
| TC-20 | 🌐 Selenium | US#5 | Header hero ocupa el ancho completo | ✅ |
| TC-21 | 🌐 Selenium | US#5 | Botón "Volver" presente en el detalle | ✅ |
| TC-22 | 👁 Manual | US#5 | Título alineado a la izquierda | ✅ |
| TC-23 | 🤖 API | US#6 | API devuelve 5 imágenes | ✅ |
| TC-24 | 🤖 API | US#6 | Primera imagen marcada como cover | ✅ |
| TC-25 | 🌐 Selenium | US#6 | Imágenes visibles en el detalle | ✅ |
| TC-26 | 🌐 Selenium | US#6 | Botón "Ver más" presente | ✅ |
| TC-27 | 🌐 Selenium | US#6 | Clic en "Ver más" abre lightbox | ✅ |
| TC-28 | 👁 Manual | US#6 | Layout desktop: imagen izq + grid 2×2 der | ✅ |
| TC-29 | 👁 Manual | US#6 | Galería responsive en mobile/tablet | ✅ |
| TC-30 | 🌐 Selenium | US#7 | Footer visible en el home | ✅ |
| TC-31 | 🌐 Selenium | US#7 | Footer contiene logo, año y copyright | ✅ |
| TC-32 | 🌐 Selenium | US#7 | Footer visible en página de detalle | ✅ |
| TC-33 | 👁 Manual | US#7 | Responsividad del footer | ✅ |
| TC-34 | 🤖 API | US#8 | Metadatos de paginación presentes | ✅ |
| TC-35 | 🤖 API | US#8 | Paginación con filtro de categoría | ✅ |
| TC-36 | 👁 Manual | US#8 | Botones Inicio · Anterior · Siguiente | ✅ |
| TC-37 | 🤖 API | US#9 | Endpoint admin accesible → HTTP 200 | ✅ |
| TC-38 | 🤖 API | US#9 | Toggle activo/inactivo cambia estado | ✅ |
| TC-39 | 🤖 API | US#9 | Editar producto actualiza datos | ✅ |
| TC-40 | 🌐 Selenium | US#9 | URL `/administracion` accesible | ✅ |
| TC-41 | 🌐 Selenium | US#9 | Botón "Lista de productos" presente | ✅ |
| TC-42 | 🌐 Selenium | US#9 | Botón "Agregar producto" presente | ✅ |
| TC-43 | 👁 Manual | US#9 | Panel no disponible en mobile | ✅ |
| TC-44 | 🤖 API | US#10 | Listado admin paginado con id y nombre | ✅ |
| TC-45 | 🤖 API | US#10 | Producto creado aparece en listado admin | ✅ |
| TC-46 | 👁 Manual | US#10 | Columnas Id, Nombre, Acciones visibles | ✅ |
| TC-47 | 🤖 API | US#11 | Eliminar producto → 204 · GET posterior → 404 | ✅ |
| TC-48 | 🤖 API | US#11 | Eliminar producto inexistente → 404 | ✅ |
| TC-49 | 👁 Manual | US#11 | Modal de confirmación al presionar Eliminar | ✅ |
| TC-50 | 👁 Manual | US#11 | Cancelar eliminación — sin cambios | ✅ |

---

### Sprint 2

#### Entregable 01 — Documentación / Bitácora

**Definición del proyecto:**
Continuación del Sprint 1. El alcance del Sprint 2 agrega autenticación completa con JWT (registro, login, logout, roles), gestión de características por vuelo con íconos Lucide, sección de categorías con imágenes, filtro multi-categoría y notificación por email al registrarse.

**Rol: Scrum Master**

| Ítem | Detalle |
|------|---------|
| **Metodología** | Scrum — iteraciones por sprint |
| **Sprint** | Sprint 2 |
| **Meta del sprint** | Autenticación JWT · Características · Categorías con imagen · Notificación email |
| **Equipo** | Full-stack (Frontend · Backend · BBDD · Infra · UX/UI · QA) |
| **Herramientas** | GitHub · Maven · Vite · Spring Security · Mailtrap (sandbox SMTP) |

**Bitácora:**

| Iteración | Actividad | Estado |
|-----------|-----------|--------|
| 1 | Spring Security + JWT stateless | ✅ |
| 2 | Endpoints `/api/auth/register` y `/api/auth/login` | ✅ |
| 3 | Entidad `User` con roles USER / ADMIN, BCrypt | ✅ |
| 4 | Página de registro con validación (solo letras en nombre) | ✅ |
| 5 | Página de login con persistencia de sesión | ✅ |
| 6 | Logout — limpieza del token JWT en localStorage | ✅ |
| 7 | Protección de rutas admin con rol ADMIN | ✅ |
| 8 | CRUD de características con ícono Lucide | ✅ |
| 9 | Asociación características ↔ vuelos desde admin | ✅ |
| 10 | Componente `CharIcon` compartido | ✅ |
| 11 | Notificación por email (`@Async`, Mailtrap sandbox) | ✅ |
| 12 | Pantalla post-registro con reenvío de confirmación | ✅ |
| 13 | Tarjetas de categorías con imagen en el home | ✅ |
| 14 | Filtro multi-categoría con selección visual | ✅ |
| 15 | CRUD de categorías con URL de imagen desde admin | ✅ |

**US completadas:** US#12 · US#13 · US#14 · US#15 · US#16 · US#17 · US#18 · US#19 · US#20 · US#21 ✅

#### Entregable 02 — Planificación y Ejecución de Tests

Ver sección [🧪 Testing](#-testing) para la estrategia, comandos de ejecución y resultados.

**Casos de prueba Sprint 2:**

| # | Tipo | US | Caso de prueba | Estado |
|---|------|----|---------------|--------|
| TC-S2-01 | 🤖 API | US#12 | Registrar usuario → HTTP 201 · token JWT | ✅ |
| TC-S2-02 | 🤖 API | US#12 | Email ya registrado → HTTP 409 | ✅ |
| TC-S2-03 | 🤖 API | US#12 | Campos vacíos → HTTP 400 | ✅ |
| TC-S2-04 | 🌐 Selenium | US#12 | Formulario de registro accesible en `/register` | ✅ |
| TC-S2-05 | 🌐 Selenium | US#12 | Registro exitoso muestra pantalla de confirmación | ✅ |
| TC-S2-06 | 🌐 Selenium | US#12 | Email duplicado muestra mensaje de error | ✅ |
| TC-S2-07 | 👁 Manual | US#12 | Solo letras en nombre y apellido | ✅ |
| TC-S2-08 | 🤖 API | US#13 | Login correcto → HTTP 200 · token JWT | ✅ |
| TC-S2-09 | 🤖 API | US#13 | Contraseña incorrecta → HTTP 401 | ✅ |
| TC-S2-10 | 🤖 API | US#13 | Email inexistente → HTTP 401 | ✅ |
| TC-S2-11 | 🌐 Selenium | US#13 | Formulario de login accesible en `/login` | ✅ |
| TC-S2-12 | 🌐 Selenium | US#13 | Login exitoso redirige al home con nombre en header | ✅ |
| TC-S2-13 | 🌐 Selenium | US#13 | Login fallido muestra mensaje de error | ✅ |
| TC-S2-14 | 👁 Manual | US#14 | Usuario autenticado ve su nombre en el header | ✅ |
| TC-S2-15 | 👁 Manual | US#14 | Sesión persiste tras recargar la página | ✅ |
| TC-S2-16 | 🌐 Selenium | US#15 | Logout elimina token y estado | ✅ |
| TC-S2-17 | 👁 Manual | US#15 | Tras logout, rutas protegidas redirigen al login | ✅ |
| TC-S2-18 | 🤖 API | US#16 | Endpoints admin con rol ADMIN → HTTP 200 | ✅ |
| TC-S2-19 | 👁 Manual | US#16 | Usuario USER no ve el panel admin | ✅ |
| TC-S2-20 | 👁 Manual | US#16 | Admin ve tabs: Vuelos · Características · Categorías · Usuarios | ✅ |
| TC-S2-21 | 🤖 API | US#17 | Crear característica → HTTP 201 · `id`, `name`, `iconName` | ✅ |
| TC-S2-22 | 🤖 API | US#17 | Editar característica → HTTP 200 · datos actualizados | ✅ |
| TC-S2-23 | 🤖 API | US#17 | Eliminar característica → HTTP 204 | ✅ |
| TC-S2-24 | 🤖 API | US#17 | Listar características → array con `name` e `iconName` | ✅ |
| TC-S2-25 | 👁 Manual | US#17 | Panel admin muestra ícono Lucide correcto | ✅ |
| TC-S2-26 | 👁 Manual | US#17 | Selector de ícono visual en modal de creación | ✅ |
| TC-S2-27 | 🤖 API | US#18 | Detalle de vuelo incluye array `characteristics` | ✅ |
| TC-S2-28 | 🌐 Selenium | US#18 | Sección "Características" visible en `/flights/1` | ✅ |
| TC-S2-29 | 👁 Manual | US#18 | Íconos Lucide correctos por característica | ✅ |
| TC-S2-30 | 👁 Manual | US#19 | Email de bienvenida enviado al registrar | ✅ |
| TC-S2-31 | 👁 Manual | US#19 | Botón "Reenviar correo" en pantalla post-registro | ✅ |
| TC-S2-32 | 👁 Manual | US#19 | Email no bloquea el registro si falla (`@Async`) | ✅ |
| TC-S2-33 | 🤖 API | US#20 | Listar categorías activas → `imageUrl`, `flightCount` | ✅ |
| TC-S2-34 | 🌐 Selenium | US#20 | Tarjetas de categorías con imagen visibles en el home | ✅ |
| TC-S2-35 | 🌐 Selenium | US#20 | Filtro por categoría actualiza vuelos mostrados | ✅ |
| TC-S2-36 | 👁 Manual | US#20 | Multi-selección de categorías filtra correctamente | ✅ |
| TC-S2-37 | 👁 Manual | US#20 | Tarjeta "Todos" restaura el listado completo | ✅ |
| TC-S2-38 | 🤖 API | US#21 | Crear categoría → HTTP 201 · `id`, `name`, `imageUrl` | ✅ |
| TC-S2-39 | 🤖 API | US#21 | Editar categoría → HTTP 200 · datos actualizados | ✅ |
| TC-S2-40 | 🤖 API | US#21 | Eliminar categoría → HTTP 204 | ✅ |
| TC-S2-41 | 👁 Manual | US#21 | Formulario incluye campo URL de imagen | ✅ |
| TC-S2-42 | 👁 Manual | US#21 | Imagen de categoría visible en tarjeta del home | ✅ |

---

### Sprint 3

#### Entregable 01 — Documentación / Bitácora

**Definición del proyecto:**
Continuación del Sprint 2. El alcance del Sprint 3 agrega buscador con selector de fechas, calendario de disponibilidad en el detalle del producto, sistema de favoritos persistente, bloque de políticas, compartir en redes sociales, sistema de puntaje/reseñas y eliminación de categorías con modal de confirmación.

**Rol: Scrum Master**

| Ítem | Detalle |
|------|---------|
| **Metodología** | Scrum — iteraciones por sprint |
| **Sprint** | Sprint 3 |
| **Meta del sprint** | Búsqueda con fechas · Disponibilidad · Favoritos · Políticas · Compartir · Reseñas · Eliminar categoría |
| **Equipo** | Full-stack (Frontend · Backend · BBDD · Infra · UX/UI · QA) |
| **Herramientas** | GitHub · Maven · Vite · react-day-picker · Spring Security |

**Bitácora:**

| Iteración | Actividad | Estado |
|-----------|-----------|--------|
| 1 | Buscador con doble selector de fechas (react-day-picker v10) | ✅ |
| 2 | Endpoint `/api/flights/search` con parámetros checkIn/checkOut | ✅ |
| 3 | Entidad `Reservation` + endpoint `/api/reservations/{id}/occupied-dates` | ✅ |
| 4 | Calendario de disponibilidad en el detalle del vuelo | ✅ |
| 5 | Entidad `Favorite` + endpoints POST/DELETE/GET `/api/favorites` | ✅ |
| 6 | Ícono de corazón en tarjetas con toggle optimista | ✅ |
| 7 | Página `/favorites` con lista de vuelos favoritos | ✅ |
| 8 | Bloque de políticas en el detalle del vuelo | ✅ |
| 9 | Modal de compartir con Facebook · Twitter · WhatsApp · Instagram | ✅ |
| 10 | Entidad `Review` + endpoints POST/GET `/api/reviews/{flightId}` | ✅ |
| 11 | Sección de reseñas y puntaje en el detalle del vuelo | ✅ |
| 12 | Actualización automática del rating promedio al agregar reseña | ✅ |
| 13 | Modal de confirmación para eliminar categoría en el panel admin | ✅ |
| 14 | Nullificación de FK en vuelos al eliminar su categoría | ✅ |
| 15 | Dependencia `spring-security-test` para tests con `@WithMockUser` | ✅ |

**US completadas:** US#22 · US#23 · US#24 · US#25 · US#26 · US#27 · US#28 · US#29 ✅

#### Entregable 02 — Planificación y Ejecución de Tests

Ver sección [🧪 Testing](#-testing) para la estrategia, comandos de ejecución y resultados.

**Casos de prueba Sprint 3:**

| # | Tipo | US | Caso de prueba | Estado |
|---|------|----|---------------|--------|
| TC-S3-01 | 🤖 API | US#22 | Búsqueda por nombre devuelve vuelos que coinciden | ✅ |
| TC-S3-02 | 🤖 API | US#22 | Búsqueda por código IATA devuelve vuelos que coinciden | ✅ |
| TC-S3-03 | 🤖 API | US#22 | Búsqueda con fechas lejanas libres devuelve vuelos | ✅ |
| TC-S3-04 | 🤖 API | US#22 | Búsqueda combinada (query + fechas) devuelve paginado correcto | ✅ |
| TC-S3-05 | 🤖 API | US#22 | Búsqueda vacía devuelve todos los vuelos activos | ✅ |
| TC-S3-06 | 🤖 API | US#22 | Endpoint recomendados devuelve array con rating | ✅ |
| TC-S3-07 | 🤖 API | US#23 | Vuelo con reservas devuelve rangos de fechas ocupadas | ✅ |
| TC-S3-08 | 🤖 API | US#23 | Vuelo sin reservas devuelve lista vacía de fechas ocupadas | ✅ |
| TC-S3-09 | 🤖 API | US#23 | Fechas ocupadas tienen formato ISO (checkIn, checkOut) | ✅ |
| TC-S3-10 | 🤖 API | US#24 | Agregar vuelo a favoritos: devuelve 200 | ✅ |
| TC-S3-11 | 🤖 API | US#24 | Agregar vuelo duplicado a favoritos es idempotente | ✅ |
| TC-S3-12 | 🤖 API | US#24 | Eliminar vuelo de favoritos: devuelve 204 | ✅ |
| TC-S3-13 | 🤖 API | US#24 | Agregar vuelo inexistente a favoritos: devuelve 404 | ✅ |
| TC-S3-14 | 🤖 API | US#25 | Listar favoritos vacíos: devuelve array vacío | ✅ |
| TC-S3-15 | 🤖 API | US#25 | Favorito aparece en lista tras agregar: contiene FlightDTO | ✅ |
| TC-S3-16 | 🤖 API | US#25 | Favorito desaparece de lista tras eliminar | ✅ |
| TC-S3-17 | 🤖 API | US#28 | Listar reseñas de vuelo: devuelve array | ✅ |
| TC-S3-18 | 🤖 API | US#28 | Crear reseña (5 estrellas): devuelve 200 con datos | ✅ |
| TC-S3-19 | 🤖 API | US#28 | Reseña con estrellas fuera de rango (6): devuelve 400 | ✅ |
| TC-S3-20 | 🤖 API | US#28 | Reseña creada aparece en el listado con stars y firstName | ✅ |
| TC-S3-21 | 🤖 API | US#28 | Rating del vuelo se actualiza tras agregar reseña | ✅ |
| TC-S3-22 | 🤖 API | US#28 | Reseña existente se actualiza con addOrUpdate (upsert) | ✅ |
| TC-S3-23 | 🤖 API | US#29 | Eliminar categoría existente: devuelve 204 | ✅ |
| TC-S3-24 | 🤖 API | US#29 | Vuelos de categoría eliminada mantienen campo category nulo | ✅ |
| TC-S3-25 | 🤖 API | US#29 | Eliminar categoría inexistente: devuelve 404 | ✅ |
| TC-S3-26 | 🤖 API | US#29 | Categoría eliminada no aparece en listado público | ✅ |
| TC-UI-S3-01 | 🌐 Selenium | US#22 | Buscador con campo de texto visible en el home | ✅ |
| TC-UI-S3-02 | 🌐 Selenium | US#22 | Botón de búsqueda presente en el buscador del home | ✅ |
| TC-UI-S3-03 | 🌐 Selenium | US#22 | Búsqueda por nombre filtra y muestra resultados | ✅ |
| TC-UI-S3-04 | 🌐 Selenium | US#22 | Sección de categorías se mantiene visible con búsqueda activa | ✅ |
| TC-UI-S3-05 | 🌐 Selenium | US#23 | Calendario de disponibilidad visible en detalle del vuelo | ✅ |
| TC-UI-S3-06 | 🌐 Selenium | US#23 | Calendario muestra el nombre del mes actual | ✅ |
| TC-UI-S3-07 | 🌐 Selenium | US#24 | Ícono de favorito visible en tarjetas con usuario autenticado | ✅ |
| TC-UI-S3-08 | 🌐 Selenium | US#24 | Botón de favorito presente en el detalle del vuelo | ✅ |
| TC-UI-S3-09 | 🌐 Selenium | US#24 | Usuario anónimo ve el home sin acceso a favoritos directos | ✅ |
| TC-UI-S3-10 | 🌐 Selenium | US#25 | Sección de favoritos accesible desde cuenta de usuario | ✅ |
| TC-UI-S3-11 | 🌐 Selenium | US#25 | Lista de favoritos muestra vuelos previamente marcados | ✅ |
| TC-UI-S3-12 | 🌐 Selenium | US#26 | Sección de políticas visible en el detalle del vuelo | ✅ |
| TC-UI-S3-13 | 🌐 Selenium | US#26 | Bloque de políticas tiene título identificable | ✅ |
| TC-UI-S3-14 | 🌐 Selenium | US#26 | Bloque de políticas ocupa el ancho del contenedor | ✅ |
| TC-UI-S3-15 | 🌐 Selenium | US#27 | Botón de compartir visible en el detalle del vuelo | ✅ |
| TC-UI-S3-16 | 🌐 Selenium | US#27 | Clic en compartir abre modal o menú con redes sociales | ✅ |
| TC-UI-S3-17 | 🌐 Selenium | US#28 | Sección de reseñas visible en el detalle del vuelo | ✅ |
| TC-UI-S3-18 | 🌐 Selenium | US#28 | Usuario autenticado ve el formulario de calificación | ✅ |
| TC-UI-S3-19 | 🌐 Selenium | US#28 | Rating numérico del vuelo visible en el detalle | ✅ |
| TC-UI-S3-20 | 🌐 Selenium | US#29 | Botón eliminar presente en la tabla de categorías del admin | ✅ |
| TC-UI-S3-21 | 🌐 Selenium | US#29 | Clic en eliminar abre modal de confirmación | ✅ |
| TC-UI-S3-22 | 🌐 Selenium | US#29 | Modal de confirmación muestra el nombre de la categoría | ✅ |
| TC-UI-S3-23 | 🌐 Selenium | US#29 | Cancelar en el modal preserva la categoría sin cambios | ✅ |
| TC-MAN-S3-01 | 👁 Manual | US#22 | Autocompletado/sugerencias al escribir en el buscador | ✅ |
| TC-MAN-S3-02 | 👁 Manual | US#22 | Selector de fechas tiene doble calendario (ida y vuelta) | ✅ |
| TC-MAN-S3-03 | 👁 Manual | US#22 | Fechas no seleccionables en el pasado | ✅ |
| TC-MAN-S3-04 | 👁 Manual | US#23 | Fechas ocupadas resaltadas en rojo/gris en el calendario | ✅ |
| TC-MAN-S3-05 | 👁 Manual | US#23 | Rango checkIn–checkOut completamente bloqueado | ✅ |
| TC-MAN-S3-06 | 👁 Manual | US#24 | Corazón relleno / vacío según estado de favorito | ✅ |
| TC-MAN-S3-07 | 👁 Manual | US#24 | Favorito persiste tras recargar la página | ✅ |
| TC-MAN-S3-08 | 👁 Manual | US#25 | Favorito aparece inmediatamente en /favorites (tiempo real) | ✅ |
| TC-MAN-S3-09 | 👁 Manual | US#25 | Eliminar favorito desde /favorites lo quita del listado | ✅ |
| TC-MAN-S3-10 | 👁 Manual | US#26 | Políticas en columnas (layout multi-columna) | ✅ |
| TC-MAN-S3-11 | 👁 Manual | US#26 | Título de la sección de políticas subrayado o destacado | ✅ |
| TC-MAN-S3-12 | 👁 Manual | US#27 | Modal compartir incluye imagen, descripción y link del vuelo | ✅ |
| TC-MAN-S3-13 | 👁 Manual | US#27 | Clic en Facebook abre URL de Facebook sharer | ✅ |
| TC-MAN-S3-14 | 👁 Manual | US#27 | Clic en Twitter/X abre URL de Twitter intent | ✅ |
| TC-MAN-S3-15 | 👁 Manual | US#28 | Estrellas interactivas — hover muestra valor antes de confirmar | ✅ |
| TC-MAN-S3-16 | 👁 Manual | US#28 | Score promedio se muestra en listado con reviewCount | ✅ |

---

## 👤 Autor

- [@JoshuaSMC](https://github.com/JoshuaSMC) — TL Frontend · TL Backend · TL BBDD · Scrum Master · UX/UI

---

## 📄 Licencia

MIT

---

*Desarrollado para Digital House — Desafío Profesional · 2026*
