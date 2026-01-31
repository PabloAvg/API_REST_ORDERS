import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product';

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private readonly baseUrl = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {}

  search(name?: string): Observable<Product[]> {
    let params = new HttpParams();
    if (name && name.trim().length > 0) {
      params = params.set('name', name.trim());
    }
    return this.http.get<Product[]>(`${this.baseUrl}/api/products`, { params });
  }
}
