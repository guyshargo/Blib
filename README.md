# Blib — Library Management System

Blib is a full-stack, client-server Java application designed for library management. It features a responsive JavaFX graphical user interface and a centralized MySQL relational database for persistent storage and inventory synchronization. 

Originally developed as an academic project using the Object Client-Server Framework (OCSF), the application is currently undergoing a complete architectural modernization to align with enterprise software engineering standards.

## 🛠 Tech Stack
* **Backend:** Java 17, Spring Boot, RESTful APIs, Embedded Tomcat
* **Frontend:** JavaFX (FXML/CSS), Java `HttpClient`, Jackson JSON Parser
* **Database:** MySQL, JDBC
* **Build Tool:** Maven

## 🏗 Architecture & Engineering Highlights
* **RESTful API Migration:** The backend network layer has been completely transitioned from raw multi-threaded TCP/IP sockets to a stateless Spring Boot REST API.
* **Strict Layered Architecture (MVC/3-Tier):** The codebase enforces a clean Separation of Concerns by isolating the Web Layer (Controllers), Data Access/Logic Layer, and Data Entities (Models).
* **Secure Resource Management:** The database logic relies entirely on the modern Java `Try-With-Resources` pattern, eliminating memory leaks by ensuring the safe, automatic closure of SQL connections and result sets across the application.

## 📚 Core Features
* **Role-Based Access Control:** Distinct control panels, permissions, and authentication flows for Subscribers and Librarians.
* **Inventory & Transaction Management:** Real-time catalog searching, book reservations, borrowing extensions, and return tracking.
* **Automated Routines:** Autonomous background threads running daily system updates, status tracking, and late-return notifications.
* **System Analytics:** Automated generation of library activity and status reports.

## 🚀 Current Project Status (WIP)
The backend architecture has been successfully rebuilt as a fully functional Spring Boot server. Active development is currently focused on the frontend—rewiring the JavaFX client controllers to replace legacy socket messaging with standard HTTP requests and JSON data mapping.