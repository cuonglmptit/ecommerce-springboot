import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { ProductApi } from '../products-api';
import { ProductApiActions, ProductFormPageActions } from './products.actions';
import { Router } from '@angular/router';

@Injectable()
export class ProductsEffects {
  private readonly actions$ = inject(Actions);
  private readonly productApi = inject(ProductApi);
  private readonly router = inject(Router);

  /**
   * Effect xử lý Submit Form tạo sản phẩm
   */
  createProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProductFormPageActions.submitForm),
      switchMap(({ payload }) =>
        this.productApi.createProduct(payload).pipe(
          map((response) =>
            ProductApiActions.createProductSuccess({ response }),
          ),
          catchError((httpError) => {
            // Lấy Map lỗi ValidationErrorsException trả về từ Backend (nếu có)
            const serverErrors =
              httpError?.error?.extra ||
              httpError?.error?.userMessage ||
              'Lỗi tạo sản phẩm.';
            return of(
              ProductApiActions.createProductFailure({ error: serverErrors }),
            );
          }),
        ),
      ),
    ),
  );

  /**
   * Chuyển hướng khi tạo sản phẩm thành công
   */
  createProductSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(ProductApiActions.createProductSuccess),
        tap(() => {
          alert('🎉 Tạo sản phẩm thành công!');
          // this.router.navigate(['/seller/products']);
        }),
      ),
    { dispatch: false },
  );
}
