# VitalDash

Web-Dashboard zur Auswertung und zum Vergleich von Vitaldaten-Zeitreihen (Diplom-/Begleitprojekt, HTL Spengergasse).

- Backend: Java 21, Spring Boot, PostgreSQL 16, Flyway
- Frontend: Angular, Chart.js
- Projektleiter: Mohamed Selmurzaev

## Projektstruktur

```
VitalDash/
  backend/     Spring-Boot-Backend (siehe backend/README.md)
  frontend/    Angular-Frontend (siehe frontend/README.md)
  db/          Setup-Skript für die lokale Datenbank
```

## Schnellstart

1. PostgreSQL 16 lokal installieren und die Datenbank einrichten:
   `psql -U postgres -f db/setup.sql`
2. Backend starten: im Ordner `backend` den Befehl `mvn spring-boot:run` ausführen
   (legt die Tabellen per Flyway an), Health-Check unter http://localhost:8080/actuator/health
3. Frontend starten: im Ordner `frontend` `npm install` und `npm start` ausführen,
   danach läuft die Anwendung auf http://localhost:4200

## Git-Konventionen

Commit-Nachrichten im Format `typ(bereich): Beschreibung`, z. B. `feat(backend): ...`,
`fix(frontend): ...`, `docs: ...`. Der Arbeitspaket-Code (z. B. `AP 1.4.2`) steht im Nachrichtentext.
