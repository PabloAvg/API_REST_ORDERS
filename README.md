# Technical Test — Order API

## Cómo levantarlo todo (lo importante)

### 1) Arrancar el backend
Desde esta carpeta:

`backend/order-api`

Ejecuta:
```bash
./mvnw spring-boot:run
```

La API quedará disponible en:
- `http://localhost:8080`

### 2) Verificar que está funcionando
#### Productos
- Listar todos:
  - `GET http://localhost:8080/api/products`
- Buscar por nombre (parcial e ignore-case):
  - `GET http://localhost:8080/api/products?name=ce`

#### Pedidos
- Crear pedido:
  - `POST http://localhost:8080/api/orders`
  - Body (JSON):
    ```json
    {
      "items": [
        { "productId": 1, "quantity": 2 },
        { "productId": 4, "quantity": 1 }
      ]
    }
    ```
- Listar pedidos:
  - `GET http://localhost:8080/api/orders`
- Detalle de pedido:
  - `GET http://localhost:8080/api/orders/1`
- Borrar pedido:
  - `DELETE http://localhost:8080/api/orders/1`

### 3) Ver la base de datos (H2 console)
Durante el desarrollo/test se usa **H2 en memoria**.

- Consola: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:obramat`
- User: `sa`
- Password: *(vacío)*

Consultas útiles:
```sql
SHOW TABLES;
SELECT * FROM product;
SELECT * FROM orders;
SELECT * FROM order_detail;
```

### 4) Ejecutar tests
Desde `backend/order-api`:
```bash
./mvnw test
```

---

## Requisitos

### Productos
- `GET /api/products`
- Búsqueda por nombre **parcial** e **insensible a mayúsculas/minúsculas** con `?name=...`

### Pedidos
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `DELETE /api/orders/{id}`
- Persistencia de pedido y líneas (`Order` / `OrderDetail`)
- Cálculo de total considerando **IVA 21%**

---

## Ejemplos (curl)

### Productos
Listar todos:
```bash
curl "http://localhost:8080/api/products"
```

Buscar por nombre:
```bash
curl "http://localhost:8080/api/products?name=ce"
```

### Pedidos
Crear pedido:
```bash
curl -X POST "http://localhost:8080/api/orders"   -H "Content-Type: application/json"   -d '{
    "items": [
      { "productId": 1, "quantity": 2 },
      { "productId": 4, "quantity": 1 }
    ]
  }'
```

Listar pedidos:
```bash
curl "http://localhost:8080/api/orders"
```

Detalle de pedido:
```bash
curl "http://localhost:8080/api/orders/1"
```

Borrar pedido:
```bash
curl -X DELETE "http://localhost:8080/api/orders/1" -i
```

---

## Detalles (decisiones técnicas)

### IVA (21%) y cálculo de precios
Este proyecto asume un **tipo único de IVA del 21%** para todos los productos.

#### Decisión del modelo de precios
Para mantener un modelo claro y estable (y evitar que los pedidos “cambien” si el precio del producto se modifica más adelante), la API usa la siguiente convención:

- **`Product.price`**: se almacena y se devuelve como **precio neto (sin IVA)**.
- **`OrderDetail.unitPrice`**: se almacena como **precio neto (sin IVA)** en el momento de crear el pedido (**snapshot**).
  - Esto garantiza consistencia histórica si el precio del producto cambia después.
- **`Order.totalPrice`**: se almacena y se devuelve como **total bruto (con IVA incluido)**.

#### Reglas de cálculo
Dado un pedido con líneas `(productId, quantity)`:

1. **Subtotal neto por línea**:
   - `lineNet = unitPrice * quantity`
2. **Total neto del pedido**:
   - `orderNet = Σ lineNet`
3. **Total bruto del pedido (con IVA)**:
   - `orderGross = round(orderNet * 1.21, 2)`

#### Redondeo y precisión monetaria
- Todas las operaciones monetarias se realizan con **`BigDecimal`**.
- El total bruto se redondea a **2 decimales** usando **HALF_UP**.
- La moneda asumida es **EUR**.

#### Ejemplo
Si un pedido contiene:
- 2 × Cemento (25.00)
- 1 × Pintura Blanca (35.50)

Total neto:
- `2*25.00 + 1*35.50 = 85.50`

Total bruto (IVA 21%):
- `85.50 * 1.21 = 103.455` → redondeado a **103.46**

---

## Calidad / mejoras implementadas

### Validación de entrada (400) y errores coherentes
- Validaciones con Jakarta Validation:
  - `items` no vacío
  - `quantity` > 0 y con límite máximo
  - no se permiten elementos `null` dentro de `items`
- Respuestas de error con estructura consistente (`timestamp`, `status`, `error`, `message`, `path`).

### Control de casos borde
- Se rechazan pedidos con **productId duplicados** en la misma creación.

### Rendimiento: evitar N+1 en el detalle de pedido
- `GET /api/orders/{id}` carga `Order` + `OrderDetail` + `Product` en una sola consulta mediante `JOIN FETCH`.

### Ordenación en el listado de pedidos
- `GET /api/orders` devuelve pedidos ordenados por `createdAt DESC`.

### CORS para desarrollo con frontend
- Se permite el consumo del API desde un frontend en `http://localhost:4200`.

### Tests automatizados
- Tests de integración (Spring Boot + MockMvc) para:
  - creación de pedido con total con IVA y `PENDING`
  - `404` al usar producto inexistente
  - `400` al usar cantidad inválida
