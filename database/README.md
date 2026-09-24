# LifeOS — Database Setup & Migrations

This directory contains database initialization scripts and setup instructions for LifeOS.

## Overview

* **Engine:** MySQL 8.0+
* **Default Database Name:** `lifeos`
* **Default Port:** `3306`
* **Character Set:** `utf8mb4`
* **Collation:** `utf8mb4_unicode_ci`
* **Migration Strategy:** **Flyway** (Authoritative schema management)
* **JPA Strategy:** `hibernate.ddl-auto: validate` (No automatic schema mutation by Hibernate)

---

## 1. Initial Setup

To create the `lifeos` database manually using the MySQL CLI or MySQL Workbench:

```bash
mysql -u root -p < database/schema.sql
```

Or execute the query directly:

```sql
CREATE DATABASE IF NOT EXISTS lifeos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

---

## 2. Environment Variables

The backend application reads database connection parameters from environment variables (with localhost development defaults):

| Environment Variable | Description | Default Value |
| :--- | :--- | :--- |
| `DB_HOST` | MySQL Server Host | `localhost` |
| `DB_PORT` | MySQL Server Port | `3306` |
| `DB_NAME` | Database Name | `lifeos` |
| `DB_USERNAME` | MySQL User | `root` |
| `DB_PASSWORD` | MySQL Password | *(empty / prompt)* |

---

## 3. Schema Migrations (Flyway)

All domain tables and schema adjustments are managed strictly through version-controlled Flyway migration files located in:

`backend/src/main/resources/db/migration/`

Naming convention:
* `V<version>__<description>.sql` (e.g., `V1__init_baseline.sql`)

When the Spring Boot application boots, Flyway automatically executes pending migrations in sequence and verifies integrity against the `flyway_schema_history` table.
