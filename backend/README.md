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

## Anmeldung und Benutzerverwaltung (AP 1.4.4)

Die Schnittstelle ist mit JWT abgesichert. Offen sind nur `POST /api/auth/login` und
der Health-Check, alle anderen Endpunkte erwarten den Header `Authorization: Bearer <token>`.

| Endpunkt | Rolle | Beschreibung |
|---|---|---|
| `POST /api/auth/login` | offen | Body `{"username","password"}`, liefert Token, Ablaufzeit und Benutzer |
| `GET /api/auth/me` | angemeldet | liefert den angemeldeten Benutzer |
| `GET /api/users` | ARZT | Benutzer auflisten |
| `POST /api/users` | ARZT | Benutzer anlegen (409 bei doppeltem Benutzernamen) |

Ohne Token antwortet die API mit 401, mit unzureichender Rolle mit 403.

Beim ersten Start legt das Backend die Demo-Benutzer `arzt` (Rolle ARZT) und `pflege`
(Rolle PFLEGEKRAFT) mit dem Passwort `demo1234` an, solange die Tabelle `app_user` leer ist.
Abschalten mit `SEED_DEMO_USERS=false`, Passwort ändern mit `DEMO_PASSWORD`.

Weitere Einstellungen über Umgebungsvariablen: `JWT_SECRET` (mindestens 32 Zeichen,
in Produktion unbedingt setzen) und `vitaldash.jwt.expiration` (Standard 8 Stunden).

Beispiel:

```
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
     -d '{"username":"arzt","password":"demo1234"}'
```

## Tests

`mvn test` im Ordner `backend`. Die Integrationstests brauchen die lokale PostgreSQL-Datenbank
(siehe oben) und rollen ihre Änderungen am Ende zurück.
