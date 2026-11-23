<h1 align="center">Hacienda VIMALU</h1>

<h3 align="center">
  Sistema web de gestión y reservas de mesas<br />
  desarrollado con Angular, Spring Boot y Oracle
</h3>

<p align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@v2.16.0/icons/angular/angular-original.svg" alt="Angular" height="40" />
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@v2.16.0/icons/typescript/typescript-original.svg" alt="TypeScript" height="40" />
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@v2.16.0/icons/bootstrap/bootstrap-original.svg" alt="Bootstrap" height="40" />
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@v2.16.0/icons/java/java-original.svg" alt="Java" height="40" />
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@v2.16.0/icons/spring/spring-original.svg" alt="Spring Boot" height="40" />
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@v2.16.0/icons/oracle/oracle-original.svg" alt="Oracle" height="40" />
</p>

<p align="center">
  <a href="#descripción-general">Descripción</a> &nbsp;·&nbsp;
  <a href="#funcionalidades">Funcionalidades</a> &nbsp;·&nbsp;
  <a href="#tecnologías">Tecnologías</a> &nbsp;·&nbsp;
  <a href="#arquitectura">Arquitectura</a> &nbsp;·&nbsp;
  <a href="#instalación">Instalación</a> &nbsp;·&nbsp;
  <a href="#equipo">Equipo</a>
</p>

---

## Descripción general

**Hacienda VIMALU** es una aplicación web para gestionar las reservas de mesas de la hacienda. El cliente elige salón, fecha, hora y mesa disponible, puede agregar productos del catálogo y registra su reserva adjuntando el voucher de Yape o Plin del pago de separación de **S/ 25**. El administrador revisa cada reserva y su voucher, gestiona productos, salones y mesas, y consulta reportes generados desde Oracle.

Proyecto integrador · línea de Software · campus Tarapoto · semestre 2026-2.

## Funcionalidades

### Cliente

<details>
<summary><b>Registro e inicio de sesión</b></summary>
<br />
Cuenta de cliente con autenticación JWT. Cada rol accede solo a sus pantallas.
</details>

<details>
<summary><b>Reserva paso a paso</b></summary>
<br />
Barra de progreso de 4 pasos: datos de la reserva; salón, fecha, hora y mesa disponible; productos; pago.
</details>

<details>
<summary><b>Catálogo de productos</b></summary>
<br />
Productos opcionales con control de stock y buscador por nombre.
</details>

<details>
<summary><b>Carrito y pago de separación</b></summary>
<br />
Resumen de la reserva, elección de Yape o Plin y carga de la captura del voucher (JPG, PNG o WEBP, hasta 1 MB). La reserva se registra como PENDIENTE y el pago como POR_VERIFICAR. No hay pasarela de pago.
</details>

<details>
<summary><b>Mis Reservas</b></summary>
<br />
Consulta de las reservas propias con el estado de la reserva y del pago.
</details>

### Administrador

<details>
<summary><b>Dashboard</b></summary>
<br />
Resumen general de reservas y ventas.
</details>

<details>
<summary><b>Gestión de reservas</b></summary>
<br />
Listado con filtros y detalle de cada reserva (cliente, salón, mesa, productos y pagos). Permite ver el voucher del cliente, aprobar o rechazar el pago y cambiar manualmente el estado de la reserva.
</details>

<details>
<summary><b>Productos, salones y mesas</b></summary>
<br />
Registro, edición y eliminación de productos (con imagen), salones y mesas.
</details>

<details>
<summary><b>Reportes</b></summary>
<br />
Ventas por período y ranking de productos más vendidos mediante procedimientos almacenados de Oracle, además de reservas por estado y clientes con más reservas.
</details>

## Tecnologías

| Capa | Tecnologías |
|---|---|
| Frontend | Angular 18 (componentes standalone, signals), TypeScript 5.5, Bootstrap 5.3, Bootstrap Icons, SweetAlert2 |
| Backend | Java 21, Spring Boot 3.4, Spring Security con JWT, Spring Data JPA, SpringDoc OpenAPI |
| Base de datos | Oracle Database XE, procedimientos almacenados (PL/SQL) |

## Arquitectura

La aplicación está separada en dos proyectos: el frontend en Angular y una API REST en Spring Boot conectada a Oracle. El modelo de reservas sigue la relación **SALÓN → MESA → RESERVA**.

<details open>
<summary><b>Estructura</b></summary>

```text
vimalu_frontend/                 # SPA en Angular
└── src/app/
    ├── core/
    │   ├── guards/              # acceso por rol (ADMIN / CLIENTE)
    │   ├── interceptors/        # token JWT en cada petición
    │   ├── interfaces/          # modelos de datos
    │   └── services/            # comunicación con la API
    ├── features/                # inicio, reserva, catálogo, carrito, mis-reservas,
    │                            # dashboard, reservas, productos, salones, mesas, reportes
    └── shared/                  # header, footer, sidebar, layouts, pasos de reserva

hacienda_vimalu/                 # API REST en Spring Boot
└── src/main/java/.../hacienda_vimalu/
    ├── config/                  # seguridad y configuración web
    ├── controller/              # endpoints REST
    ├── dto/                     # objetos de transferencia
    ├── entity/                  # entidades JPA
    ├── exception/               # manejo de errores
    ├── repository/              # acceso a datos y procedimientos almacenados
    ├── security/                # JWT
    └── services/                # reglas de negocio
```

</details>

## Instalación

### Requisitos previos

- **Node.js** con npm y **Angular CLI 18**
- **Java JDK 21**
- **Oracle Database XE**

### 1. Backend

<details>
<summary><b>API en Spring Boot</b></summary>
<br />

Configura la conexión a Oracle en `src/main/resources/application.yaml` y ejecuta:

```bash
cd hacienda_vimalu
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8085/hacienda_vimalu` y su documentación en `/hacienda_vimalu/swagger-ui.html`.

</details>

### 2. Frontend

<details>
<summary><b>Aplicación Angular</b></summary>
<br />

```bash
cd vimalu_frontend
npm install
npm start
```

La aplicación se abre en `http://localhost:4200`. El archivo `proxy.config.json` redirige las peticiones `/api` al backend.

</details>

## Equipo

<div align="center">
<table>
  <tr>
    <td align="center" width="200">
      <a href="https://github.com/nicoViena">
        <img src="https://github.com/nicoViena.png?size=100" width="90" alt="Nicolas Viena" /><br />
        <sub><b>Nicolas Viena</b></sub>
      </a><br />
      <sub>@nicoViena</sub>
    </td>
    <td align="center" width="200">
      <a href="https://github.com/JoshoSysX">
        <img src="https://github.com/JoshoSysX.png?size=100" width="90" alt="Josias Guillermo" /><br />
        <sub><b>Josias Guillermo</b></sub>
      </a><br />
      <sub>@JoshoSysX</sub>
    </td>
    <td align="center" width="200">
      <a href="https://github.com/Breysys">
        <img src="https://github.com/Breysys.png?size=100" width="90" alt="Breysys" /><br />
        <sub><b>Breysys</b></sub>
      </a><br />
      <sub>@Breysys</sub>
    </td>
    <td align="center" width="200">
      <a href="https://github.com/lester01-apps">
        <img src="https://github.com/lester01-apps.png?size=100" width="90" alt="Leester" /><br />
        <sub><b>Leester</b></sub>
      </a><br />
      <sub>@lester01-apps</sub>
    </td>
  </tr>
</table>
</div>
