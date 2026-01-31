export interface CreateOrderItemRequest {
  productId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  items: CreateOrderItemRequest[];
}

export interface OrderSummaryResponse {
  id: number;
  createdAt: string;
  status: 'PENDING' | 'CANCELLED' | 'PAID';
  totalPrice: number; // bruto (con IVA)
}

export interface OrderItemResponse {
  productId: number;
  productName: string;
  unitPrice: number; // neto
  quantity: number;
  subtotal: number;  // neto
}

export interface OrderResponse {
  id: number;
  createdAt: string;
  status: 'PENDING' | 'CANCELLED' | 'PAID';
  totalPrice: number; // bruto (con IVA)
  items: OrderItemResponse[];
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
