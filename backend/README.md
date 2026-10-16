# VitalDash Backend

Spring Boot 3 (Java 21), PostgreSQL 16, Flyway.

## Voraussetzungen

- JDK 21 und Maven
- PostgreSQL 16 (lokal installiert, Port 5432)

## Datenbank einrichten (einmalig)

Im Projektordner, mit dem PostgreSQL-Superuser `postgres`:

```
psql -U postgres -f db/setup.sql
```

Das Skript legt den Benutzer `vitaldash` und die Datenbank `vitaldash` an.
Die Tabellen erzeugt das Backend beim Start selbst über Flyway.

## Starten

1. Backend starten: `mvn spring-boot:run` (im Ordner `backend`)
2. Health-Check: http://localhost:8080/actuator/health

Die Datenbankverbindung lässt sich über die Umgebungsvariablen `DB_URL`, `DB_USER`
und `DB_PASSWORD` ändern (Standard: lokale Datenbank `vitaldash`).
