
# 🌐 Angular Tournament Client

***This Frontend project was build and deployed using Google Gemini 3 pro using promtps to create section and validating the correct functionality. This should not intent to be used in a Production environment or replace any Predictor interface.***

## 1. Project Overview

This project represents the **Frontend layer** of the Microservices ecosystem. It is a Single Page Application (SPA) built with **Angular** designed to interact with the Java Backend (Tournament Core & Oracle Engine).

The application is containerized using **Docker** and served via **Nginx** for production environments, ensuring high performance and efficient static content delivery.

## 2. Architecture & Design Pattern

The application follows a **Component-Service-Model** architecture to ensure separation of concerns and maintainability.

### 🏗 Architectural Layers:

1. **Presentation Layer (Components):**
* Responsible for the UI/UX and user interaction.
* **Smart Components (Pages):** Manage state and communicate with services (e.g., `DashboardComponent`).
* **Dumb Components:** Purely presentational components receiving data via `@Input()` and emitting events via `@Output()`.


2. **Logic Layer (Services):**
* Injectable classes that handle business logic.
* Act as a bridge between the Components and the Backend API.
* Utilize **RxJS** for state management and handling asynchronous data streams.


3. **Data Layer (Models/Interfaces):**
* TypeScript interfaces mirroring the DTOs from the Java Backend to ensure type safety (e.g., `Tournament`, `MatchResult`).


4. **Infrastructure Layer (Docker + Nginx):**
* **Multi-stage Build:** Compiles the TypeScript code to optimized JavaScript.
* **Reverse Proxy:** Nginx is configured to serve the static files and handle routing (SPA fallback).



## 3. Technology Stack

* **Framework:** Angular (v17+)
* **Language:** TypeScript 5.x
* **Styling:** SCSS / CSS
* **HTTP Client:** Angular `HttpClientModule`
* **Notifications:** `SweetAlert2`
* **Build Tool:** Angular CLI & Node.js (v20+)
* **Containerization:** Docker & Nginx Alpine

## 4. API Integration & Endpoints

The frontend communicates with the microservices via RESTful APIs. The base URL is configured in `src/environments/environment.ts`.

### Connection Flow

1. **Request:** Angular Service sends an HTTP Request -> Nginx (Container) -> Java Backend.
2. **Response:** Backend processes logic -> Returns JSON -> Angular Service (Observable) -> Component View.

### Key Endpoints (Examples)

| Method | Endpoint | Description | Service Responsible |
| --- | --- | --- | --- |
| `GET` | `/matches/scheduled` | Fetches the list of active and scheduled matches. | `TournamentService` |
| `PUT` | `/matches/{id}/finish` | Update and fininsh a match posting the score for the ```{id}``` match. | `TournamentService` |
| `GET` | `/predict` | Retrieves prediction for scheduled matches using winning percentages for every Team. | `OracleService` |

*> **Note:** All endpoints require valid CORS configuration on the backend side.*

## 5. Setup & Installation

### Prerequisites

* Node.js v20 or higher.
* NPM (Node Package Manager).
* Docker & Docker Compose (for containerized execution).

### A. Local Development (Without Docker)

1. **Install Dependencies:**
```bash
npm install

```


2. **Run Development Server:**
```bash
ng serve

```


Access the app at `http://localhost:4200/`.

### B. Docker Deployment (Recommended)

This project is optimized for Docker. It uses a **Multi-stage build** process to keep the image light.

1. **Build and Run via Docker Compose:**
(From the root of the entire project)
```bash
docker-compose up --build -d angular-client

```


2. **Access the Application:**
Go to `http://localhost` (Served via Nginx on port 80).

## 6. Project Structure

```text
/src
  /app
    /core           # Singleton services, interceptors, and guards
    /shared         # Reusable components, pipes, and directives
    /pages          # Main route components (Dashboard, Login, etc.)
      /dashboard
    /models         # TypeScript interfaces (DTOs)
    /services       # API communication logic
  /assets           # Static images and icons
  /environments     # Env configs (Dev vs Prod URLs)
Dockerfile          # Multi-stage build definition
nginx.conf          # Web server configuration
angular.json        # CLI workspace configuration
package.json        # Dependencies and scripts

```

## 7. Configuration & Troubleshooting

### **Budget Settings**

The `angular.json` is configured with strict bundle budgets to monitor performance.

* **Warning:** 10kb (Component Styles)
* **Error:** 20kb (Component Styles)
* *Fix:* If the build fails due to size, verify imports or adjust the budget in `angular.json`.

### **Nginx Routing (404/403 Errors)**

The `nginx.conf` includes a `try_files` directive:

```nginx
try_files $uri $uri/ /index.html;

```

This ensures that refreshing the page on a sub-route (e.g., `/dashboard`) does not cause a 404 error, delegating the routing back to Angular.

### **Docker Volume Mapping**

If you see a 403 Forbidden error or an empty page, ensure the `COPY` instruction in the Dockerfile correctly targets the `dist/` output folder (check if a `/browser` subfolder exists).