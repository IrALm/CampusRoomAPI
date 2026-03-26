# CampusRoom API

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-Database-000000?style=for-the-badge&logo=database&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![JUnit 5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-788BD2?style=for-the-badge&logo=mockito&logoColor=white)

Bienvenue sur l'API CampusRoom. Ce projet backend est construit avec Spring Boot et intègre des bases de données en mémoire (H2) pour la gestion des campus, des salles, des utilisateurs et des réservations de manière centralisée. Il suit une architecture structurée avec l'implémentation robuste de design patterns.

## 🚀 Installation & Démarrage

### Prérequis

*   Git
*   Java 21
*   Maven

### 1. Cloner le dépôt

Via HTTPS :
```bash
git clone https://github.com/IrALm/CampusRoomAPI.git
```

### 2. Lancer l'environnement

Assurez-vous d'avoir Java 21 et Maven installés, puis lancez l'application avec la commande suivante :

```bash
mvn spring-boot:run
```
| Service              | URL / Commande                                                   | Identifiants / Info                                                                                     |
| :------------------- | :--------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------ |
| **API Backend**      | `http://localhost:8095/`                                         | Point d'entrée de l'API                                                                                 |
| **Swagger UI**       | [Accéder à Swagger](http://localhost:8095/swagger-ui/index.html) | Documentation interactive de l'API                                                                      |
| **H2 Database**      | `http://localhost:8095/h2-console`                               | **JDBC URL:** `jdbc:h2:mem:campusdb`<br>**User:** `sa`<br>**Password:** (vide)                          |

## 🧪 Tests

Le projet intègre des tests unitaires et une vérification de la couverture de code via **JaCoCo**.

```bash
# Lancer les tests manuellement
mvn clean test
```
```bash
# Lancer Jacoco pour voir la couverture des tests
mvn jacoco:report
```

## 🏗️ Architecture Backend

L'architecture backend s'appuie sur une **architecture multicouche (Controller-Service-Repository)**, permettant une meilleure maintenabilité et évolutivité du code. Les concepts de création et de validation de réservation sont isolés via les Design Patterns **Factory** et **Strategy**.

```mermaid
graph TD
    classDef mainNode fill:#ffcc80,stroke:#e65100,color:black,stroke-width:2px;
    classDef svcNode fill:#fff9c4,stroke:#fbc02d,color:black,stroke-width:2px;
    classDef patNode fill:#e1bee7,stroke:#4a148c,color:black,stroke-width:2px;
    classDef repNode fill:#b2dfdb,stroke:#004d40,color:black,stroke-width:2px;

    API["Contrôleurs RESTful<br/>(Campus, Room, User, Reservation)"]:::mainNode
    Service["Couche Service<br/>Logique Métier Centrale"]:::svcNode
    Pattern["Design Patterns<br/>(Factory, Strategy)"]:::patNode
    Data["Couche Données<br/>(Repositories, H2 DB)"]:::repNode

    API --> Service
    Service --> Pattern
    Service --> Data
```

Une documentation technique très détaillée sur l'architecture et les Design Patterns implémentés se trouve sur ce site https://iralm.github.io/CampusRoomAPI/
