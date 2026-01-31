## IVA (21%) y cálculo de precios

Este proyecto asume un **tipo único de IVA del 21%** para todos los productos, tal y como exige la prueba técnica.

### Decisión del modelo de precios
Para mantener un modelo claro y estable (y evitar que los pedidos “cambien” si el precio del producto se modifica más adelante), la API usa la siguiente convención:

- **`Product.price`**: se almacena y se devuelve como **precio neto (sin IVA)**.
- **`OrderDetail.unitPrice`**: se almacena como **precio neto (sin IVA)** en el momento de crear el pedido (snapshot).
  - Esto garantiza consistencia histórica si el precio del producto cambia después.
- **`Order.totalPrice`**: se almacena y se devuelve como **total bruto (con IVA incluido)**.

### Reglas de cálculo
Dado un pedido con líneas `(productId, quantity)`:

1. **Subtotal neto por línea**:
   - `lineNet = unitPrice * quantity`
2. **Total neto del pedido**:
   - `orderNet = Σ lineNet`
3. **Total bruto del pedido (con IVA)**:
   - `orderGross = round(orderNet * 1.21, 2)`

### Redondeo y precisión monetaria
- Todas las operaciones monetarias se realizan con **`BigDecimal`**.
- El total bruto se redondea a **2 decimales** usando **HALF_UP** (redondeo comercial estándar).
- La moneda asumida es **EUR**.

### Consistencia de la salida de la API
- `/api/products` devuelve **precios netos** (sin IVA).
- `/api/orders/{id}` devuelve:
  - `unitPrice` y el `subtotal` de cada línea como **importes netos**
  - `totalPrice` como **importe bruto (con IVA incluido)**

### Ejemplo
Si un pedido contiene:
- 2 × Cemento (25.00)
- 1 × Pintura Blanca (35.50)

Total neto:
- `2*25.00 + 1*35.50 = 85.50`

Total bruto (IVA 21%):
- `85.50 * 1.21 = 103.455` → redondeado a **103.46**
