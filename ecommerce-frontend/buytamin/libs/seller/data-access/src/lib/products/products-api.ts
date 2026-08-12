import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CreateProductRequest, ProductsEntity } from './products.models';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductApi {
  private readonly http = inject(HttpClient);

  private readonly baseUrl = '/api/v1/products';

  /**
   * API Tạo mới sản phẩm
   */
  createProduct(payload: CreateProductRequest): Observable<ProductsEntity> {
    return this.http.post<ProductsEntity>(this.baseUrl, payload);
  }

  /**
   * API Lấy danh sách sản phẩm
   */
  getProducts(): Observable<ProductsEntity[]> {
    return this.http.get<ProductsEntity[]>(this.baseUrl);
  }
}
