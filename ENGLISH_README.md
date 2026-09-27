# TeamSphere

TeamSphere is a full-stack web application designed to centralize and
simplify the management of people, tasks, events, reports, and
organizational information for small organizations.

The project was developed as a professional portfolio application, with
a focus on backend architecture, authentication, multi-organization data
isolation, REST APIs, cloud deployment, database management, and AI
integration.

## 🌐 Live Demo

**Frontend:**\
https://teamsphere-frontend.onrender.com

### Demo account

``` text
Email: demonstration@teamsphere.com
Password: Demonstration123
```

> The demo account is provided for testing purposes.

------------------------------------------------------------------------

## 🎯 Problem

Small organizations often manage their members, tasks, events, and
internal information across different tools or manually.

TeamSphere provides a centralized platform where an organization can
manage these processes from a single interface.

The application also demonstrates how AI can be integrated into an
existing business application to provide contextual assistance based on
the organization's data.

------------------------------------------------------------------------

## ✨ Main Features

-   🔐 JWT-based authentication
-   👥 Member management
-   🏢 Multi-organization architecture
-   ✅ Task management
-   📅 Event management
-   📊 Reports and dashboards
-   🔎 Global search
-   🤖 AI Assistant
-   🌙 Dark and light themes
-   🌎 Spanish / English interface
-   📱 Responsive interface
-   ☁️ Cloud deployment

------------------------------------------------------------------------

## 🤖 AI Assistant

TeamSphere includes an AI Assistant integrated with Google's Gemini API.

The assistant can answer questions about information available within
the authenticated user's organization, including members, tasks, events,
and reports.

The AI context is generated on the backend using the authenticated
user's identity and organization. The frontend does not manually send
the organization ID. The backend determines the organization from the
authenticated JWT and provides Gemini only with data belonging to that
organization.

### Example

> "How many members are in my organization?"

The backend retrieves the relevant data and provides contextual
information to Gemini, allowing the assistant to return a
natural-language response.

------------------------------------------------------------------------

## 🏢 Multi-Organization Architecture

TeamSphere supports multiple organizations within the same application.

Each member belongs to an organization, and organization-specific data
such as tasks, events, reports, and members is filtered according to the
authenticated user's organization.

For demonstration purposes, organizations are currently created and
associated through the backend, simulating the type of organization
provisioning that could normally occur during onboarding or a
subscription process.

------------------------------------------------------------------------

## 🛠️ Technologies

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

### Database

-   PostgreSQL
-   Neon

### AI

-   Google Gemini API

### Deployment

-   Render --- Frontend
-   Render --- Backend
-   Neon --- PostgreSQL

------------------------------------------------------------------------

## 🏗️ Architecture

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

## 🔐 Security

The application implements:

-   JWT authentication
-   Password hashing
-   Protected API endpoints
-   Role-based authorization
-   CORS configuration
-   Organization-level data isolation
-   Backend-controlled AI context

Sensitive credentials such as database passwords, JWT secrets, and API
keys are stored using environment variables and are not included in the
repository.

------------------------------------------------------------------------

## ☁️ Deployment

The production environment is structured as:

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

The frontend communicates with the deployed Spring Boot API through
HTTPS.

------------------------------------------------------------------------

## 📂 Project Structure

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

## 🚀 Running Locally

### Backend

``` bash
cd backend
./mvnw spring-boot:run
```

Backend:

``` text
http://localhost:8080
```

### Frontend

``` bash
cd frontend
npm install
npm run dev
```

Frontend:

``` text
http://localhost:5173
```

### Environment Variables

Backend:

``` text
DATABASE_URL
DATABASE_USERNAME
DB_PASSWORD
JWT_SECRET
GEMINI_API_KEY
```

Frontend:

``` text
VITE_API_URL
```

------------------------------------------------------------------------

## 📌 Project Status

TeamSphere is currently deployed and functional in a production
environment.

The project was built as a portfolio application to demonstrate
full-stack development, cloud deployment, database architecture,
authentication, API design, and practical AI integration.

------------------------------------------------------------------------

## 👨‍💻 Author

**Miguel Ocampo**

Systems Engineering --- Software Development

GitHub:\
https://github.com/mocampo03
