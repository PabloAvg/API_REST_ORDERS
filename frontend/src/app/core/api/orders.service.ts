import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateOrderRequest, OrderResponse } from '../models/order';

@Injectable({ providedIn: 'root' })
export class OrdersService {
  private readonly baseUrl = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {}

  create(request: CreateOrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.baseUrl}/api/orders`, request);
  }
}
