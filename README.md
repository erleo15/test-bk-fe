# Sistema de Gestión de Transferencias Interbancarias

Solución integral fullstack para la gestión de cuentas bancarias y transferencias de fondos, desarrollada con **Quarkus 3 (Java 21)** y **Angular 19**.

---

## 1. Requisitos Previos

- **Docker** y **Docker Compose** (recomendado para despliegue completo)
- Si deseas compilar y ejecutar localmente sin Docker:
  - **Java 21 LTS**
  - **Maven 3.9+**
  - **Node.js 20+** y **npm**
  - **MySQL 8.0+**

---

## 2. Despliegue Rápido con Docker Compose (Recomendado)

El proyecto incluye un entorno preconfigurado multi-contenedor que levanta la base de datos MySQL, el backend compilado en modo nativo con GraalVM, el servicio de migración inicial de datos (`import.sql`) y el frontend Angular servido vía Nginx.

### 2.1. Inicio estándar (con caché de capas Docker)
Recompila automáticamente solo si detecta cambios en el código fuente:

```bash
cd ci-cd
docker compose up -d --build
```

### 2.2. Recompilación limpia forzada (sin caché)
Para obligar a Docker a ejecutar las pruebas unitarias y recompilar el binario nativo de Quarkus y el bundle de Angular desde cero:

```bash
cd ci-cd
docker compose build --no-cache && docker compose up -d
```

### 2.3. Verificación y Detención de Servicios

Para verificar que todos los servicios estén operativos (`healthy`):

```bash
docker compose ps
```

Para detener los contenedores:

```bash
docker compose down
```

---

## 3. URLs de Acceso

- **Frontend Web (Angular):** [http://localhost:4200](http://localhost:4200)
- **Backend API:** [http://localhost:8080/api](http://localhost:8080/api)
- **Documentación Swagger / OpenAPI:** [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

---

## 4. Ejecución Local (Paso a Paso)

### 4.1. Configuración de Base de Datos (MySQL)

1. Crear la base de datos:
   ```sql
   CREATE DATABASE test_bk_fe_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Cargar el esquema y datos iniciales ejecutando el script:
   ```bash
   mysql -u root -p test_bk_fe_prod < ci-cd/scripts/import.sql
   ```

### 4.2. Compilación y Ejecución del Backend

1. Dirigirse al directorio del backend:
   ```bash
   cd backend
   ```
2. Ejecutar las pruebas automatizadas:
   ```bash
   mvn test
   ```
3. Compilar el proyecto:
   ```bash
   mvn clean package
   ```
4. Iniciar el backend en modo desarrollo:
   ```bash
   mvn quarkus:dev
   ```

### 4.3. Ejecución del Frontend

1. Dirigirse al directorio del frontend:
   ```bash
   cd frontend
   ```
2. Instalar dependencias:
   ```bash
   npm install
   ```
3. Iniciar el servidor de desarrollo:
   ```bash
   npm start
   ```
   Acceder en el navegador a `http://localhost:4200`.

---

## 5. Endpoints Principales (API REST)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/cuentas` | Listado paginado de cuentas (`page`, `pageSize`, `sortBySaldo`) |
| `POST` | `/api/cuentas` | Registro de una nueva cuenta bancaria |
| `GET` | `/api/cuentas/{id}/resumen` | Resumen detallado y saldo de una cuenta |
| `POST` | `/api/transferencias` | Ejecución atómica de transferencias entre cuentas |
| `GET` | `/api/transferencias` | Historial de transferencias con filtros |

---

## 6. Autor

- **Hernán León**

