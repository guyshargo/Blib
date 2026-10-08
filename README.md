# Blib — Library Management System

Blib is a full-stack library platform for managing book inventory, borrowing, member records, and librarian workflows. The project has moved from the legacy desktop/client-server approach to a modern web-based architecture centered on a Spring Boot backend and a React frontend.

## Current project stage
The repository is currently in an active modernization and integration phase:

- The backend is built as a Spring Boot REST API and exposes endpoints for authentication, catalog browsing, member management, borrowing, returns, reports, and invoice activity.
- The frontend is implemented in React with Vite and includes dedicated pages for catalog browsing, member login, librarian dashboards, borrow/return flows, and reports.
- The remaining focus is on final API/frontend wiring, validation, and polishing the user experience.

## Tech stack
- Backend: Java 17, Spring Boot 3, REST APIs, Maven
- Frontend: React 19, TypeScript, Vite, React Router
- Database: MySQL 8
- UI: Tailwind CSS

## Project status
Blib is currently in an active web migration and feature-completion phase. The modern Spring Boot + React stack is in place, the core domain flows are present, and the project is being refined toward a fully integrated library-management application.