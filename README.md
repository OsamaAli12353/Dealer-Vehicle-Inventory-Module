# Dealer & Vehicle Inventory Module

A multi-tenant Inventory module built with Spring Boot that manages dealers and their vehicles.

---

## Tech Stack
- Java 25
- Spring Boot 4.0.4
- Spring Security
- Spring Data JPA
- Hibernate 7
- MySQL
- Lombok

---

## Architecture
Clean layered architecture:

| Layer | Description |
|-------|-------------|
| **Controller** | Handles HTTP requests |
| **Service** | Business logic |
| **Repository** | Database access |
| **Entity** | Database models |
| **DTO** | Request/response objects |
| **Security** | Tenant isolation & authentication |
| **Exception** | Global error handling |

---

## Multi-Tenancy
Every request requires `X-Tenant-Id` header.

| Case | Result |
|------|--------|
| Missing `X-Tenant-Id` header | `400 Bad Request` |
| Cross-tenant access | `403 Forbidden` |
| Valid tenant | `200 OK` |

---

## Authentication
Basic Auth — set credentials in `application.properties`:
```properties
spring.security.user.name=admin
spring.security.user.password=1234
```

---

## Installation & Setup

### Prerequisites
Make sure you have these installed:
- [Java 25](https://jdk.java.net/25/)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL](https://dev.mysql.com/downloads/installer/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (recommended)
- [Postman](https://www.postman.com/downloads/) (for testing)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/OsaMa/Dealer-Vehicle-Inventory-Module.git
cd Dealer-Vehicle-Inventory-Module
```

**2. Create the database**
```sql
CREATE DATABASE inventory_db;
```

**3. Configure the application**

Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db?createDatabaseIfNotExist=true
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.security.user.name=admin
spring.security.user.password=1234
```

**4. Run the application**

Option A — IntelliJ:
- Open the project
- Run `Task3Application.java`

Option B — Terminal:
```bash
mvn spring-boot:run
```

**5. Verify it's running**

Open your browser and go to:
```
http://localhost:8080/dealers
```
You should see a login popup — enter your credentials from `application.properties`.

**6. Test with Postman**

Every request needs these headers:

| Header | Value |
|--------|-------|
| `X-Tenant-Id` | `tenant_1` or `tenant_2` |
| `Authorization` | Basic Auth (admin / 1234) |

---

## API Endpoints

### Dealers

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/dealers` | Create a dealer |
| `GET` | `/dealers` | Get all dealers (paginated) |
| `GET` | `/dealers/{id}` | Get dealer by ID |
| `PATCH` | `/dealers/{id}` | Update a dealer |
| `DELETE` | `/dealers/{id}` | Delete a dealer |

### Vehicles

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/vehicles` | Create a vehicle |
| `GET` | `/vehicles` | Get all vehicles (paginated + filters) |
| `GET` | `/vehicles/{id}` | Get vehicle by ID |
| `PATCH` | `/vehicles/{id}` | Update a vehicle |
| `DELETE` | `/vehicles/{id}` | Delete a vehicle |

### Admin

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/admin/dealers/countBySubscription` | Count dealers by subscription type |

> Requires `X-Role: GLOBAL_ADMIN` header.
> Returns a **global count** across all tenants.

---

## Vehicle Filters

| Param | Type | Description |
|-------|------|-------------|
| `model` | String | Filter by model name (case-insensitive) |
| `status` | Enum | `AVAILABLE` or `SOLD` |
| `priceMin` | Decimal | Minimum price |
| `priceMax` | Decimal | Maximum price |
| `subscription` | Enum | `PREMIUM` → only vehicles from PREMIUM dealers |
| `page` | Integer | Page number (default: 0) |
| `size` | Integer | Page size (default: 10) |
| `sort` | String | e.g. `price,asc` or `model,desc` |

---

## Example Requests

**Get all dealers**
```
GET http://localhost:8080/dealers
X-Tenant-Id: tenant_1
Authorization: Basic Auth (admin / 1234)
```

**Create a dealer**
```
POST http://localhost:8080/dealers
X-Tenant-Id: tenant_1
Content-Type: application/json

{
  "name": "Toyota Cairo",
  "email": "toyota@cairo.com",
  "subscriptionType": "PREMIUM"
}
```

**Create a vehicle**
```
POST http://localhost:8080/vehicles
X-Tenant-Id: tenant_1
Content-Type: application/json

{
  "dealerId": "your-dealer-uuid",
  "model": "Camry 2024",
  "price": 250000.00,
  "status": "AVAILABLE"
}
```

**Filter vehicles**
```
GET http://localhost:8080/vehicles?status=AVAILABLE&priceMin=200000&priceMax=500000&sort=price,asc
X-Tenant-Id: tenant_1
```

**PREMIUM vehicles only**
```
GET http://localhost:8080/vehicles?subscription=PREMIUM
X-Tenant-Id: tenant_1
```

**Admin count**
```
GET http://localhost:8080/admin/dealers/countBySubscription
X-Tenant-Id: tenant_1
X-Role: GLOBAL_ADMIN
```

Expected response:
```json
{
  "BASIC": 10,
  "PREMIUM": 10
}
```

---

## Data Model

### Dealer
| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Auto-generated |
| `tenantId` | String | Tenant identifier |
| `name` | String | Dealer name |
| `email` | String | Unique per tenant |
| `subscriptionType` | Enum | `BASIC` or `PREMIUM` |

### Vehicle
| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Auto-generated |
| `tenantId` | String | Tenant identifier |
| `dealerId` | UUID | FK to dealer |
| `model` | String | Vehicle model |
| `price` | Decimal | Vehicle price |
| `status` | Enum | `AVAILABLE` or `SOLD` |

---

## Error Responses

| Status | Meaning |
|--------|---------|
| `400` | Missing `X-Tenant-Id` header or validation error |
| `403` | Cross-tenant access or missing `GLOBAL_ADMIN` role |
| `404` | Resource not found |
| `500` | Unexpected server error |
