# VitalDash Frontend

Angular 20 (Standalone Components), Chart.js mit chartjs-plugin-zoom.

## Starten

```
npm install
npm start
```

Die Anwendung läuft danach auf http://localhost:4200.

## Weitere Befehle

- `npm run build` Produktions-Build
- `npm test` Unit-Tests (Karma/Jasmine, Chrome erforderlich)

## Projektstruktur

```
src/app/
  core/       Dienste, Interceptor, Guards (Login, JWT)
  shared/     wiederverwendbare Komponenten und Pipes
  features/   Fachbereiche, jeweils ein Ordner
    dashboard/  Stations-Dashboard
```

Anfragen an `/api` leitet der Dev-Server über `proxy.conf.json` an das Backend
(http://localhost:8080) weiter.
