# Technical Test — Obramat (Backend + Frontend)

Este repo tiene **dos partes**:

- `backend/order-api` → API Spring Boot (productos + pedidos)
- `frontend/` → Angular (pantalla para crear pedidos)

La idea es simple: **buscas productos**, los metes en un pedido, ajustas cantidades, ves el total (con IVA) y lo mandas al backend.

---

## Cómo levantarlo todo (lo importante)

### 1) Backend (Spring Boot)
En una terminal:

```bash
cd backend/order-api
./mvnw spring-boot:run
```

Backend en:
- http://localhost:8080

### 2) Frontend (Angular)
En otra terminal:

```bash
cd frontend
npm install
npx ng serve -o
```

Frontend en:
- http://localhost:4200

> Para que el buscador y el botón de crear pedido funcionen, el backend tiene que estar levantado.

---

## Demo rápida (lo que enseñaría en la entrevista)

1) Entra a http://localhost:4200
2) Escribe en “Buscar producto” (ej. `ce` o `pint`)
3) Selecciona productos del desplegable
4) Ajusta cantidades en la tabla
5) Revisa neto / IVA (21%) / total
6) Pulsa **Crear pedido**
7) Te sale un mensaje de éxito y se vacía el carrito

---

## Ver la base de datos (H2 Console)

La API usa **H2 en memoria** (una base de datos ligera para pruebas).
Se borra al reiniciar el backend.

- Consola: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:obramat`
- User: `sa`
- Password: *(vacío)*

Consultas útiles:
```sql
SHOW TABLES;
SELECT * FROM product;
SELECT * FROM orders ORDER BY id DESC;
SELECT * FROM order_detail ORDER BY id DESC;
```

---

## Endpoints del backend (resumen)

### Productos
- `GET /api/products`
- `GET /api/products?name=ce` (búsqueda parcial, sin importar mayúsculas/minúsculas)

### Pedidos
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `DELETE /api/orders/{id}`

---

## Tests

### Backend
Desde `backend/order-api`:
```bash
./mvnw test
```

### Frontend
Desde `frontend`:
```bash
npx ng test --watch=false
```

---

## IVA (21%) y cómo se calculan los precios

La prueba pide IVA fijo del **21%**.

Para no liarme (y para que los pedidos no cambien “por arte de magia” si mañana cambia el precio de un producto), he usado esta convención:

- `Product.price` → **neto (sin IVA)**
- `OrderDetail.unitPrice` → **neto (sin IVA) “fotografiado”** al crear el pedido (snapshot)
- `Order.totalPrice` → **bruto (con IVA)**

Reglas:
- Subtotal línea (neto) = `unitPrice * quantity`
- Neto pedido = suma de subtotales
- Total (con IVA) = `round(neto * 1.21, 2)` usando **BigDecimal** en backend

En la UI muestro:
- Neto
- IVA (21%)
- Total

(El backend sigue siendo la fuente de verdad del total guardado).

---

## Cosas “extra” que metí porque ayudan (sin pasarse)

- Validaciones y errores consistentes (400/404 con mensaje claro)
- Evitar N+1 en el detalle del pedido (fetch join)
- Ordenación de pedidos por fecha desc
- CORS para que Angular (4200) pueda llamar al backend (8080)
- Tests: integración en backend + unitarios sencillos en frontend (cálculo de totales)

---

## Estructura del repo

```
backend/order-api     # Spring Boot API
frontend/             # Angular app
README.md
```
