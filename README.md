# Retail Orders — Full‑stack demo (Spring Boot + Angular)

**Live demo (frontend):** https://pabloavg.github.io/API_REST_ORDERS/
**API (backend):** https://api-rest-orders.onrender.com
**Repo:** https://github.com/PabloAvg/API_REST_ORDERS

> **Nota sobre la demo:** el backend está desplegado en **Render (plan free)**. Si lleva un rato sin uso, puede “dormirse” y la **primera petición tarda ~30–60s** (cold start). Después, las siguientes van rápidas.

---

## Qué hace
- Buscar productos por nombre (búsqueda parcial, sin distinguir mayúsculas/minúsculas).
- Añadir productos a un pedido, cambiar cantidades, eliminar líneas.
- Ver **Neto / IVA (21%) / Total** en tiempo real.
- Enviar el pedido al backend y persistirlo (H2 en memoria).

---

## Cómo probarlo rápido
1) Abre la **Live demo**: https://pabloavg.github.io/API_REST_ORDERS/
2) Espera la primera carga si Render está “despierto”.
3) Escribe `ce` o `pint` en “Buscar producto”, añade líneas, cambia cantidades y pulsa **Crear pedido**.

---

## Endpoints principales
### Productos
- `GET /api/products`
- `GET /api/products?name=ce`

### Pedidos
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `DELETE /api/orders/{id}`

---

## Ejecutar en local
### Backend
```bash
cd backend/order-api
./mvnw spring-boot:run
```
API en: http://localhost:8080

### Frontend
```bash
cd frontend
npm install
npx ng serve -o
```
App en: http://localhost:4200

> Para que el buscador y “Crear pedido” funcionen en local, el backend debe estar levantado.

---

## Despliegue (resumen)
- **Frontend**: GitHub Pages (build Angular con `--base-href "/API_REST_ORDERS/"`).
- **Backend**: Render (Web Service).

---

## Postman
- Collection: `postman/retail.postman_collection.json`
- (Opcional) Environment: `postman/retail.postman_environment.json`

Variable `baseUrl`:
- Local: `http://localhost:8080`
- Render: `https://api-rest-orders.onrender.com`

---

## IVA (21%) y precios
Convención usada para mantener consistencia histórica:

- `Product.price`: **neto** (sin IVA).
- `OrderDetail.unitPrice`: **neto** “capturado” al crear el pedido (snapshot).
- `Order.totalPrice`: **bruto** (con IVA) redondeado a 2 decimales.

Cálculo:
- Subtotal línea (neto) = `unitPrice * quantity`
- Neto pedido = suma subtotales
- Total (con IVA) = `round(neto * 1.21, 2)`

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

## H2 (solo local)
Consola: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:retail`
- User: `sa`
- Password: *(vacío)*
