# B2B Commercial Management API

## Project Overview
This application is a backend REST API designed to digitize the commercial operations of a B2B hardware distributor. It centralizes client portfolio management, inventory tracking, and complex order processing. The system is engineered to handle high-value transactions with strict financial validation and an automated, rule-based loyalty engine.

## Core Business Logic

### 1. Dynamic Loyalty Engine
The application moves beyond static customer categorization by implementing a dynamic tiering algorithm. Client status is re-evaluated automatically after every confirmed transaction based on two aggregated metrics: Life-to-Date Transaction Volume and Cumulative Revenue.

* **Tier Progression:** Clients advance through Basic, Silver, Gold, and Platinum tiers automatically when historical thresholds are met.
* **Conditional Discounting:** Discounts are not applied universally. The system utilizes a compound validation logic where the discount percentage is determined by the intersection of the Client's Current Tier and the Order Subtotal. This ensures that higher tier benefits are only unlocked when specific order value thresholds are reached, protecting margins on smaller transactions.

### 2. Multi-Instrument Financial Settlement
The payment architecture supports split-tender transactions to accommodate B2B payment flexibility. A single sales order can be settled using multiple distinct payment entries across different instruments (Cash, Check, and Bank Transfer).

* **Zero-Balance Validation:** The system enforces a strict state machine on order lifecycles. An order cannot transition from PENDING to CONFIRMED until the computed `Remaining Balance` equals exactly zero.
* **Fiscal Compliance:** Value Added Tax (VAT) is calculated strictly on the Net Value (Post-Discount) to ensure compliance with standard tax regulations.

### 3. Inventory Integrity
The system employs a Soft-Delete pattern for product management. Discontinued items are flagged rather than physically removed from the database schema. This design choice guarantees referential integrity for historical sales reports and audit logs, ensuring that past invoices remain renderable even after a product is removed from the active catalog.

## Technical Architecture

* **Pattern:** Layered Architecture adhering to Domain-Driven Design (DDD) principles (Controller, Service, Repository, DTO).
* **Security:** Custom Role-Based Access Control (RBAC) implemented via HttpSession Interceptors to segregate Administrative and Client contexts.
* **Precision:** All monetary calculations utilize `BigDecimal` with specific rounding modes to prevent floating-point errors inherent in financial software.
* **Tech Stack:** Java 17, Spring Boot 4, mySQL, JUnit 5.
