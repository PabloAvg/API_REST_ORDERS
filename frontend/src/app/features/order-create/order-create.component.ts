import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { BehaviorSubject, Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, startWith, switchMap } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { ProductsService } from '../../core/api/products.service';
import { OrdersService } from '../../core/api/orders.service';
import { Product } from '../../core/models/product';
import { ApiError, CreateOrderRequest } from '../../core/models/order';
import { grossTotal, netTotal, vatAmount, round2 } from '../../core/utils/money';

type CartLine = { product: Product; quantity: number };

@Component({
  selector: 'app-order-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './order-create.component.html',
})
export class OrderCreateComponent {
  searchCtrl = new FormControl<string | Product>('', { nonNullable: true });

  private readonly cartSubject = new BehaviorSubject<CartLine[]>([]);
  cart$: Observable<CartLine[]> = this.cartSubject.asObservable();

  filteredProducts$: Observable<Product[]> = this.searchCtrl.valueChanges.pipe(
    startWith(this.searchCtrl.value),
    debounceTime(250),
    map(v => this.normalizeQuery(v)),
    distinctUntilChanged(),
    switchMap(q => this.productsService.search(q))
  );

  displayedColumns: string[] = ['name', 'unitPrice', 'quantity', 'subtotal', 'actions'];

  net$ = this.cart$.pipe(map(lines => netTotal(lines.map(l => ({ unitPrice: l.product.price, quantity: l.quantity })))));
  vat$ = this.net$.pipe(map(n => vatAmount(n)));
  gross$ = this.net$.pipe(map(n => grossTotal(n)));

  canSubmit$ = this.cart$.pipe(map(lines => lines.length > 0 && lines.every(l => l.quantity > 0)));

  constructor(
    private readonly productsService: ProductsService,
    private readonly ordersService: OrdersService,
    private readonly snackBar: MatSnackBar
  ) {}

  displayProduct = (p: Product | string): string => (typeof p === 'string' ? p : p?.name ?? '');

  onProductSelected(ev: MatAutocompleteSelectedEvent): void {
    const product = ev.option.value as Product;
    this.addToCart(product);
    this.searchCtrl.setValue('');
  }

  addToCart(product: Product): void {
    const current = this.cartSubject.value;
    const idx = current.findIndex(l => l.product.id === product.id);

    if (idx >= 0) {
      const updated = [...current];
      updated[idx] = { ...updated[idx], quantity: updated[idx].quantity + 1 };
      this.cartSubject.next(updated);
      return;
    }

    this.cartSubject.next([...current, { product, quantity: 1 }]);
  }

  updateQuantity(productId: number, rawValue: string): void {
    const q = Number(rawValue);
    const quantity = Number.isFinite(q) ? Math.max(0, Math.trunc(q)) : 0;

    const updated = this.cartSubject.value.map(l =>
      l.product.id === productId ? { ...l, quantity } : l
    );
    this.cartSubject.next(updated);
  }

  removeLine(productId: number): void {
    this.cartSubject.next(this.cartSubject.value.filter(l => l.product.id !== productId));
  }

  lineSubtotal(line: CartLine): number {
    return round2(line.product.price * line.quantity);
  }

  createOrder(): void {
    const lines = this.cartSubject.value;
    if (lines.length === 0 || lines.some(l => l.quantity <= 0)) {
      this.snackBar.open('El pedido debe tener al menos un producto y cantidades > 0.', 'OK', { duration: 3000 });
      return;
    }

    const request: CreateOrderRequest = {
      items: lines.map(l => ({ productId: l.product.id, quantity: l.quantity })),
    };

    this.ordersService.create(request).subscribe({
      next: (order) => {
        this.snackBar.open(`Pedido creado (ID ${order.id}) — Total ${order.totalPrice.toFixed(2)}€`, 'OK', { duration: 4000 });
        this.cartSubject.next([]);
        this.searchCtrl.setValue('');
      },
      error: (err: HttpErrorResponse) => {
        const api = err.error as ApiError | undefined;
        const msg = api?.message ?? 'Error creando el pedido';
        this.snackBar.open(msg, 'OK', { duration: 4000 });
      }
    });
  }

  private normalizeQuery(v: string | Product): string {
    return typeof v === 'string' ? v : (v?.name ?? '');
  }
}
