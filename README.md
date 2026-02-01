# Retail Orders demo full stack

Live demo frontend
https://pabloavg.github.io/API_REST_ORDERS/

API backend
https://api-rest-orders.onrender.com

Repo
https://github.com/PabloAvg/API_REST_ORDERS

Nota sobre la demo
El backend está en Render en plan gratis. Si lleva un rato sin uso, la primera petición puede tardar alrededor de 30 a 60 segundos. Después va rápido.

---

## Qué hace
- Buscar productos por nombre con búsqueda parcial y sin distinguir mayúsculas
- Añadir productos a un pedido, cambiar cantidades y eliminar líneas
- Mostrar neto, IVA 21 y total en tiempo real
- Crear el pedido en el backend

---

## Probarlo online
1. Abre la live demo
2. Escribe ce o pint en el buscador
3. Añade productos y ajusta cantidades
4. Pulsa crear pedido y revisa el resumen

---

## Ejecutar en local

### Backend
```bash
cd backend/order-api
./mvnw spring-boot:run
```
API en
http://localhost:8080

### Frontend
```bash
cd frontend
npm install
npx ng serve -o
```
App en
http://localhost:4200

---

## Endpoints
Productos
- GET /api/products
- GET /api/products?name=ce

Pedidos
- POST /api/orders
- GET /api/orders
- GET /api/orders/{id}
- DELETE /api/orders/{id}

---

## Tests
Backend
```bash
cd backend/order-api
./mvnw test
```

Frontend
```bash
cd frontend
npx ng test --watch=false
```

---

## Postman
- Colección: postman/retail.postman_collection.json
- Entorno: postman/retail.postman_environment.json

Variable baseUrl
- Local: http://localhost:8080
- Render: https://api-rest-orders.onrender.com

---

## IVA y precios
Convención usada para mantener consistencia histórica
- Product.price es precio neto sin IVA
- OrderDetail.unitPrice es precio neto guardado al crear el pedido
- Order.totalPrice es total bruto con IVA redondeado a 2 decimales

Cálculo
- Subtotal línea neto = unitPrice por quantity
- Neto pedido = suma de subtotales
- Total con IVA = redondeo de neto por 1.21 a 2 decimales

---

## Decisiones técnicas
- Separación por capas en backend con controller, service y repository
- DTOs separados de entidades para no exponer el modelo JPA
- Validaciones en request de pedidos y mensajes de error claros en 400 y 404
- Cálculo monetario con BigDecimal y redondeo comercial a 2 decimales
- Carga de detalle de pedidos con fetch join para evitar problemas de lazy loading
- Frontend con servicios para HTTP, modelos tipados y util de dinero para cálculos
- CORS configurado para local y para la demo en Pages

---

## Limitaciones
- Render puede tardar en la primera carga por el modo reposo del plan gratis
- H2 es en memoria, los datos se reinician al reiniciar el backend
- No hay autenticación ni persistencia real, no se pedía para la prueba
