# TeamSphere

TeamSphere es una aplicación web full-stack diseñada para centralizar y
simplificar la gestión de personas, tareas, eventos, reportes e
información organizacional para pequeñas organizaciones.

El proyecto fue desarrollado como una aplicación profesional de
portafolio, con énfasis en arquitectura backend, autenticación,
aislamiento de datos entre organizaciones, APIs REST, despliegue en la
nube, gestión de bases de datos e integración con inteligencia
artificial.

## 🌐 Demo en línea

**Frontend:**\
https://teamsphere-frontend.onrender.com

### Cuenta de demostración

``` 
Correo: demonstration@teamsphere.com
Contraseña: Demonstration123
```

> La cuenta de demostración se proporciona para fines de prueba.

------------------------------------------------------------------------

## 🎯 Problemática

Las pequeñas organizaciones suelen gestionar sus miembros, tareas,
eventos e información interna utilizando diferentes herramientas o
procesos manuales.

TeamSphere proporciona una plataforma centralizada donde una
organización puede gestionar estos procesos desde una sola interfaz.

La aplicación también demuestra cómo la inteligencia artificial puede
integrarse en una aplicación empresarial existente para proporcionar
asistencia contextual basada en los datos de la organización.

------------------------------------------------------------------------

## ✨ Funcionalidades principales

-   🔐 Autenticación mediante JWT
-   👥 Gestión de miembros
-   🏢 Arquitectura multi-organización
-   ✅ Gestión de tareas
-   📅 Gestión de eventos
-   📊 Reportes y dashboards
-   🔎 Búsqueda global
-   🤖 Asistente de IA
-   🌙 Tema claro y oscuro
-   🌎 Interfaz en español e inglés
-   📱 Interfaz responsive
-   ☁️ Despliegue en la nube

------------------------------------------------------------------------

## 🤖 Asistente de IA

TeamSphere incluye un Asistente de IA integrado con la API de Google
Gemini.

El asistente puede responder preguntas sobre la información disponible
dentro de la organización del usuario autenticado, incluyendo miembros,
tareas, eventos y reportes.

El contexto utilizado por la IA se genera en el backend utilizando la
identidad del usuario autenticado y su organización. El frontend no
envía manualmente el ID de la organización. El backend determina la
organización mediante el JWT autenticado y proporciona a Gemini
únicamente los datos correspondientes a esa organización.

### Ejemplo

> "¿Cuántos miembros tiene mi organización?"

El backend obtiene la información correspondiente y proporciona el
contexto a Gemini, permitiendo que el asistente genere una respuesta en
lenguaje natural.

------------------------------------------------------------------------

## 🏢 Arquitectura multi-organización

TeamSphere permite trabajar con múltiples organizaciones dentro de la
misma aplicación.

Cada miembro pertenece a una organización, y los datos específicos de
cada organización ---como tareas, eventos, reportes y miembros--- se
filtran según la organización del usuario autenticado.

Para efectos de demostración, las organizaciones actualmente se crean y
asocian mediante el backend, simulando el tipo de proceso de
aprovisionamiento que normalmente podría ocurrir durante un onboarding o
una suscripción.

------------------------------------------------------------------------

## 🛠️ Tecnologías

### Frontend

-   React 19
-   Vite
-   JavaScript / JSX
-   Axios
-   Lucide React
-   CSS

### Backend

-   Java 17
-   Spring Boot 4
-   Spring Security
-   JWT
-   Spring Data JPA
-   Hibernate
-   REST API
-   Maven

### Base de datos

-   PostgreSQL
-   Neon

### Inteligencia artificial

-   Google Gemini API

### Despliegue

-   Render --- Frontend
-   Render --- Backend
-   Neon --- PostgreSQL

------------------------------------------------------------------------

## 🏗️ Arquitectura

```
React + Vite
     │
     │ REST API
     ▼
Spring Boot Backend
     │
     ├──────────► Neon PostgreSQL
     │
     └──────────► Google Gemini API
```

------------------------------------------------------------------------

## 🔐 Seguridad

La aplicación implementa:

-   Autenticación mediante JWT
-   Hashing de contraseñas
-   Endpoints protegidos
-   Autorización basada en roles
-   Configuración CORS
-   Aislamiento de datos por organización
-   Contexto de IA controlado desde el backend

Las credenciales sensibles, como contraseñas de base de datos, secretos
JWT y claves de API, se almacenan mediante variables de entorno y no se
incluyen en el repositorio.

------------------------------------------------------------------------

## ☁️ Despliegue

El entorno de producción está estructurado de la siguiente manera:

```
React/Vite
     │
     ▼
Render Static Site
     │
     ▼
Spring Boot API
     │
     ├──────────► Neon PostgreSQL
     │
     └──────────► Google Gemini API
```

El frontend se comunica con el backend desplegado mediante HTTPS.

------------------------------------------------------------------------

## 📂 Estructura del proyecto

```
TeamSphere/
│
├── backend/
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── mvnw
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
├── README.md
└── README.es.md
```

------------------------------------------------------------------------

## 🚀 Ejecución local

### Backend

``` bash
cd backend
./mvnw spring-boot:run
```

Backend:

```
http://localhost:8080
```

### Frontend

``` bash
cd frontend
npm install
npm run dev
```

Frontend:

```
http://localhost:5173
```

### Variables de entorno

Backend:

```
DATABASE_URL
DATABASE_USERNAME
DB_PASSWORD
JWT_SECRET
GEMINI_API_KEY
```

Frontend:

```
VITE_API_URL
```

------------------------------------------------------------------------

## 📌 Estado del proyecto

TeamSphere se encuentra actualmente desplegado y funcional en un entorno
de producción.

El proyecto fue desarrollado como una aplicación de portafolio para
demostrar conocimientos de desarrollo full-stack, despliegue en la nube,
arquitectura de bases de datos, autenticación, diseño de APIs e
integración práctica de inteligencia artificial.

------------------------------------------------------------------------

## 👨‍💻 Autor

**Miguel Ocampo**

Ingeniería en Sistemas --- Desarrollo de Software

GitHub:\
https://github.com/mocampo03
