import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { AttributeInfo, AttributeOptionInfo } from './attribute.models';
import { ApiResponse } from '@buytamin/shared/models';

@Injectable({
  providedIn: 'root',
})
export class AttributeApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/attributes';

  /**
   * Tìm kiếm Tên Thuộc tính (Màu sắc, Kích thước...)
   */
  searchAttributes(
    shopId: number,
    query: string,
  ): Observable<AttributeInfo[]> {
    return this.http
      .get<ApiResponse<AttributeInfo[]>>(`${this.baseUrl}/search`, {
        params: { shopId, query },
      })
      .pipe(map((res) => res.data || []));
  }

  /**
   * Tìm kiếm Giá trị Phân loại theo Ngữ cảnh Tên Thuộc tính (Đỏ, Xanh, S, M...)
   */
  searchOptions(
    shopId: number,
    attributeName: string,
    query: string,
  ): Observable<AttributeOptionInfo[]> {
    return this.http
      .get<ApiResponse<AttributeOptionInfo[]>>(
        `${this.baseUrl}/options/search`,
        {
          params: { shopId, attributeName, query },
        },
      )
      .pipe(map((res) => res.data || []));
  }
}
