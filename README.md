# java-shareit
## 📦 ShareIt — Service zum Teilen von Gegenständen
ShareIt ist ein REST-Service, der es Nutzern ermöglicht, Gegenstände zu teilen, Mietanfragen zu erstellen und Dinge zu finden, die aktuell nicht frei verfügbar sind.
Das Projekt ist mit Java 11 / Spring Boot / JPA / H2/PostgreSQL unter Verwendung einer Multi-Modul-Architektur realisiert.

### 🚀 Hauptfunktionen
#### 🔹 Benutzerverwaltung
- Erstellen von Benutzern
- Bearbeiten von Benutzerdaten
- Löschen von Benutzern
- Abrufen von Benutzerinformationen

#### 🔹 Artikelverwaltung (Items)

- Erstellen neuer Gegenstände
- Bearbeiten von Informationen
- Suche nach Gegenständen über Name und Beschreibung
- Anzeige der Artikelliste eines Besitzers

#### 🔹 Buchung von Gegenständen (Booking)

- Erstellen einer Buchungsanfrage
- Bestätigung oder Ablehnung der Buchung durch den Besitzer
- Einsicht in die Buchungshistorie
- Abrufen aktueller und zukünftiger Buchungen

#### 🔹 Artikelanfragen (Item Requests)

- Erstellen einer Anfrage für einen nicht verfügbaren Gegenstand
- Einsehen von Anfragen anderer Nutzer
- Antworten auf Anfragen

### 🏗 Architektur

Das Projekt ist in folgende Module unterteilt:

- gateway — nimmt HTTP-Anfragen entgegen, validiert Daten und leitet sie an den Server weiter (Proxy)
- server — enthält die Kern-Businesslogik, Datenbankanbindung, JPA und Services
- common — DTOs, gemeinsame Modelle und Mapper
Jedes Modul ist unabhängig und wird separat getestet.

### 🛠 Verwendete Technologien

- Java 11
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 (Tests) / PostgreSQL (Production)
- Docker / docker-compose
- Lombok
- JUnit 5
- Maven


## 📦 ShareIt — сервис для шеринга вещей

ShareIt — это REST-сервис, который позволяет пользователям делиться вещами, оформлять запросы на аренду и находить предметы, которых нет в свободном доступе.
Проект реализован на Java 11 / Spring Boot / JPA / H2/PostgreSQL в соответствии с многомодульной архитектурой.

### 🚀 Основные возможности
#### 🔹 Управление пользователями

- Создание пользователей
- Редактирование данных пользователя
- Удаление
- Получение информации о пользователе

#### 🔹 Управление вещами

- Создание новой вещи
- Редактирование информации
- Поиск вещей по названию и описанию
- Просмотр списка вещей владельца

#### 🔹 Бронирование вещей

- Создание запроса на бронирование
- Подтверждение или отклонение бронирования владельцем
- Просмотр истории бронирований
- Получение текущих и будущих бронирований

#### 🔹 Запросы на вещи (Item Requests)

- Создание запроса на вещь, которой нет в наличии
- Получение запросов других пользователей
- Ответы на запросы

### 🏗 Архитектура

Проект состоит из модулей:

- gateway — принимает HTTP-запросы, валидирует данные, проксирует на сервер
- server — основная бизнес-логика, база данных, JPA, сервисы
- common — DTO, общие модели и мапперы

Каждый модуль независим и тестируется отдельно.

### 🛠 Используемые технологии

- Java 11
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 (тесты) / PostgreSQL (production)
- Docker / docker-compose
- Lombok
- JUnit 5
- Maven


### 🗄 Datenbank-Konfiguration
🧪 Test-Konfiguration (H2)

### 🗄 Конфигурация БД
🧪 Тестовая конфигурация (H2)

Datei/Файл 

src/test/resources/application.properties:
```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
```
### 💾 Produktion (PostgreSQL)
### 💾 Продакшн (PostgreSQL)

Beispiel:
Пример:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/shareit
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=none
```
### 📚 Beispiele für REST-Anfragen
### 📚 Примеры REST-запросов

#### ▶ Benutzer erstellen
#### ▶ Создать пользователя

POST /users
```
{
  "name": "Alex",
  "email": "alex@example.com"
}
```
#### ▶ Artikel erstellen
#### ▶ Создать вещь

POST /items
Header: X-Sharer-User-Id: 1
```
{
  "name": "Bohrhammer",
  "description": "Leistungsstark, wie neu",
  "available": true
}
```
#### ▶ Artikel buchen
#### ▶ Забронировать вещь

POST /bookings
Header: X-Sharer-User-Id: 2
```
{
  "itemId": 1,
  "start": "2026-03-15T10:00:00",
  "end": "2026-03-20T10:00:00"
}
```
#### ▶ Buchung durch Besitzer bestätigen
#### ▶ Подтвердить бронирование владельцем

PATCH /bookings/1?approved=true
Header: X-Sharer-User-Id: 1

#### ▶ Artikelanfrage erstellen
#### ▶ Создать запрос на вещь

POST /requests
Header: X-Sharer-User-Id: 3
```
{
  "description": "Suche eine Bohrmaschine für das Wochenende"
}
```

#### ▶ Artikel per Textsuche finden
#### ▶ Найти вещи по тексту
```
GET /items/search?text=bohrmaschine
```
