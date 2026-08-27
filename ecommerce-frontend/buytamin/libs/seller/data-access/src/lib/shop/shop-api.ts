import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '@buytamin/shared/models';
import { ShopInfo } from './shop.models';

@Injectable({
  providedIn: 'root',
})
export class ShopApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/shops';

  /**
   * Lấy thông tin Cửa hàng của tài khoản đang đăng nhập (/me)
   */
  getMyShop(): Observable<ShopInfo> {
    return this.http
      .get<ApiResponse<ShopInfo>>(`${this.baseUrl}/me`)
      .pipe(map((res) => res.data));
  }
}
