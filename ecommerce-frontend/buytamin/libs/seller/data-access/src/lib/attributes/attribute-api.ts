import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { AttributeInfoDTO, AttributeOptionInfoDTO } from './attribute.models';

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AttributeApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/v1/attributes';

  /**
   * Tìm kiếm Tên Thuộc tính (Màu sắc, Kích thước...)
   */
  searchAttributes(
    shopId: number,
    query: string,
  ): Observable<AttributeInfoDTO[]> {
    return this.http
      .get<ApiResponse<AttributeInfoDTO[]>>(`${this.baseUrl}/search`, {
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
  ): Observable<AttributeOptionInfoDTO[]> {
    return this.http
      .get<ApiResponse<AttributeOptionInfoDTO[]>>(
        `${this.baseUrl}/options/search`,
        {
          params: { shopId, attributeName, query },
        },
      )
      .pipe(map((res) => res.data || []));
  }
}
