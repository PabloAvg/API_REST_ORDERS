# Prueba técnica retail — Backend + Frontend

Este repositorio tiene dos partes:

- `backend/order-api`: API en Spring Boot (productos y pedidos)
- `frontend`: app en Angular para crear pedidos desde una pantalla sencilla

La app permite buscar productos, añadirlos a un pedido, ajustar cantidades, ver el total (con IVA) y enviarlo al backend.

---

## Cómo arrancarlo

### Backend
Desde la carpeta del backend:

```bash
cd backend/order-api
./mvnw spring-boot:run
```

Queda disponible en:
- http://localhost:8080

### Frontend
En otra terminal, desde la carpeta del frontend:

```bash
cd frontend
npm install
npx ng serve -o
```

Se abre en:
- http://localhost:4200

> Para que el buscador y “Crear pedido” funcionen, el backend debe estar levantado.

---

## Uso rápido (para comprobar que todo funciona)

1) Abre http://localhost:4200
2) En “Buscar producto”, escribe algo como `ce` o `pint`
3) Selecciona productos del desplegable
4) Cambia las cantidades en la tabla
5) Comprueba neto / IVA (21%) / total
6) Pulsa **Crear pedido** y verás un mensaje de confirmación

---

## Base de datos (H2)

El backend usa H2 en memoria (solo para esta prueba).
Los datos se reinician cuando paras y vuelves a arrancar el backend.

Consola:
- http://localhost:8080/h2-console

Credenciales:
- JDBC URL: `jdbc:h2:mem:retail`
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

## Endpoints del backend

### Productos
- `GET /api/products`
- `GET /api/products?name=ce` (búsqueda parcial, sin distinguir mayúsculas/minúsculas)

### Pedidos
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `DELETE /api/orders/{id}`

---

## Tests

### Backend
```bash
cd backend/order-api
./mvnw test
```

### Frontend
```bash
cd frontend
npx ng test --watch=false
```

---

## IVA (21%) y precios

La prueba pide un IVA fijo del 21%. Para que los pedidos sean consistentes, se ha usado esta convención:

- `Product.price`: precio neto (sin IVA)
- `OrderDetail.unitPrice`: precio neto “guardado” al crear el pedido (snapshot)
- `Order.totalPrice`: total bruto (con IVA)

Cálculo:
- Subtotal por línea (neto) = `unitPrice * quantity`
- Neto del pedido = suma de subtotales
- Total (con IVA) = redondeo a 2 decimales de `neto * 1.21`

En el frontend se muestran neto, IVA y total, pero el valor que se guarda y se devuelve como total del pedido es el del backend.

---

## Detalles que se han cuidado

- Validaciones y mensajes de error claros (400/404)
- Carga del detalle de pedido evitando problemas típicos de N+1 (fetch join)
- Ordenación de pedidos por fecha descendente
- CORS configurado para que Angular (4200) pueda llamar al backend (8080)
- Tests de integración en backend y tests unitarios sencillos en frontend (cálculo de totales)

---

## Estructura

```
backend/order-api
frontend
README.md
```
