-- VitalDash: Datenbankschema V1 (laut Vorstudie V1.0, Kapitel 7)
-- AP 1.4.2 Datenbank implementieren

-- Benutzer mit Rolle (Passwörter nur als Hash, z. B. BCrypt)
CREATE TABLE app_user (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_app_user_role CHECK (role IN ('PFLEGEKRAFT', 'ARZT'))
);

-- Patienten (id entspricht der Patient_ID der CSV)
CREATE TABLE patient (
    id   BIGINT       PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Ein Messwert pro Patient und Tag; NULL = Messausfall
CREATE TABLE vital_measurement (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    patient_id   BIGINT   NOT NULL REFERENCES patient (id),
    measured_on  DATE     NOT NULL,
    bp_systolic  SMALLINT,
    bp_diastolic SMALLINT,
    heart_rate   SMALLINT,
    resp_rate    SMALLINT,
    spo2         SMALLINT,
    temperature  NUMERIC(3, 1),
    is_anomaly   BOOLEAN  NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_vital_patient_day UNIQUE (patient_id, measured_on),
    CONSTRAINT ck_vital_spo2 CHECK (spo2 IS NULL OR spo2 BETWEEN 0 AND 100)
);

-- Warnungen (werden beim Import für Anomalie-Zeilen erzeugt)
CREATE TABLE alert (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    patient_id      BIGINT      NOT NULL REFERENCES patient (id),
    measurement_id  BIGINT      NOT NULL UNIQUE REFERENCES vital_measurement (id),
    type            VARCHAR(20) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'OFFEN',
    acknowledged_by BIGINT      REFERENCES app_user (id),
    acknowledged_at TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_alert_type   CHECK (type IN ('GRENZWERT', 'MESSAUSFALL')),
    CONSTRAINT ck_alert_status CHECK (status IN ('OFFEN', 'QUITTIERT')),
    -- Quittierung dokumentiert immer wer und wann
    CONSTRAINT ck_alert_ack CHECK (
        (status = 'OFFEN'     AND acknowledged_by IS NULL     AND acknowledged_at IS NULL) OR
        (status = 'QUITTIERT' AND acknowledged_by IS NOT NULL AND acknowledged_at IS NOT NULL)
    )
);

-- Klinisches Tagebuch (Löschen als Soft-Delete über deleted_at)
CREATE TABLE clinical_note (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    patient_id BIGINT      NOT NULL REFERENCES patient (id),
    author_id  BIGINT      NOT NULL REFERENCES app_user (id),
    content    TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ
);

-- Importprotokoll für CSV-Importe
CREATE TABLE import_log (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    filename      VARCHAR(255) NOT NULL,
    imported_by   BIGINT       REFERENCES app_user (id),
    started_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    rows_total    INTEGER      NOT NULL DEFAULT 0,
    rows_imported INTEGER      NOT NULL DEFAULT 0,
    rows_rejected INTEGER      NOT NULL DEFAULT 0,
    status        VARCHAR(20)  NOT NULL DEFAULT 'LAEUFT',
    message       TEXT,
    CONSTRAINT ck_import_status CHECK (status IN ('LAEUFT', 'ERFOLGREICH', 'FEHLGESCHLAGEN'))
);

-- Indizes für typische Abfragen
CREATE INDEX idx_alert_status       ON alert (status);
CREATE INDEX idx_alert_patient      ON alert (patient_id);
CREATE INDEX idx_note_patient_alive ON clinical_note (patient_id) WHERE deleted_at IS NULL;
