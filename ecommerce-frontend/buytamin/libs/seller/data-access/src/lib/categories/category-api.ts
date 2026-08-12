import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '@buytamin/shared/models';
import {
  CategoryAttributeInfo,
  CategoryTreeNode,
} from './category.models';

@Injectable({
  providedIn: 'root',
})
export class CategoryApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/categories';

  // API Lấy toàn bộ Cây Danh mục
  getCategoryTree(): Observable<ApiResponse<CategoryTreeNode[]>> {
    return this.http.get<ApiResponse<CategoryTreeNode[]>>(
      `${this.baseUrl}/tree`,
    );
  }

  // Lấy danh sách thuộc tính liên kết với Category (SPECIFICATION / VARIATION)
  getCategoryAttributes(
    categoryId: number,
    type?: 'SPECIFICATION' | 'VARIATION',
  ): Observable<ApiResponse<CategoryAttributeInfo[]>> {
    const params: Record<string, string> = {};
    if (type) params['type'] = type;

    return this.http.get<ApiResponse<CategoryAttributeInfo[]>>(
      `${this.baseUrl}/${categoryId}/attributes`,
      { params },
    );
  }
  /*
  // Lấy danh mục gốc (Level 0)
  getRootCategories(): Observable<ApiResponse<CategoryInfoView[]>> {
    return this.http.get<ApiResponse<CategoryInfoView[]>>(
      `${this.baseUrl}/roots`,
    );
  }

  // Lấy danh mục con trực tiếp theo parentId
  getChildrenCategories(
    parentId: number,
  ): Observable<ApiResponse<CategoryInfoView[]>> {
    return this.http.get<ApiResponse<CategoryInfoView[]>>(
      `${this.baseUrl}/${parentId}/children`,
    );
  }

  // Tìm kiếm danh mục theo tên cho Typeahead/Search
  searchCategories(
    keyword: string,
  ): Observable<ApiResponse<CategoryInfoView[]>> {
    return this.http.get<ApiResponse<CategoryInfoView[]>>(
      `${this.baseUrl}/search`,
      {
        params: { q: keyword },
      },
    );
  }

  // Lấy danh sách thuộc tính liên kết với Category (SPECIFICATION / VARIATION)
  getCategoryAttributes(
    categoryId: number,
    type?: 'SPECIFICATION' | 'VARIATION',
  ): Observable<ApiResponse<CategoryAttributeInfoView[]>> {
    const params: Record<string, string> = {};
    if (type) {
      params['type'] = type;
    }
    return this.http.get<ApiResponse<CategoryAttributeInfoView[]>>(
      `${this.baseUrl}/${categoryId}/attributes`,
      { params },
    );
  }
   */
}
