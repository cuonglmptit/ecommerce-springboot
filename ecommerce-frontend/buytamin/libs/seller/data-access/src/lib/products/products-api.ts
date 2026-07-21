import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CreateProductRequest, ProductsEntity } from './products.models';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root', // Đăng ký global tự động cho toàn app
})
export class ProductApi {
  private readonly http = inject(HttpClient);

  // URL endpoint tới backend Spring Boot của bạn
  private readonly baseUrl = 'http://localhost:8080/api/v1/products';

  /**
   * API Tạo mới sản phẩm
   */
  createProduct(payload: CreateProductRequest): Observable<ProductsEntity> {
    return this.http.post<ProductsEntity>(this.baseUrl, payload);
  }

  /**
   * API Lấy danh sách sản phẩm (dùng cho hành động init)
   */
  getProducts(): Observable<ProductsEntity[]> {
    return this.http.get<ProductsEntity[]>(this.baseUrl);
  }
}
