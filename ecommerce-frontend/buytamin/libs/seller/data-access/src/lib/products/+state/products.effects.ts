import { Injectable, inject } from '@angular/core';
import { createEffect, Actions, ofType } from '@ngrx/effects';
import { switchMap, catchError, of, map } from 'rxjs';
import * as ProductsActions from './products.actions';
import * as ProductsFeature from './products.reducer';
import { ProductApiActions, ProductFormPageActions } from './products.actions';
import { ProductApi } from '../products-api';

@Injectable()
export class ProductsEffects {
  private actions$ = inject(Actions);
  private readonly productApi = inject(ProductApi);

  init$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProductsActions.initProducts),
      switchMap(() =>
        of(ProductsActions.loadProductsSuccess({ products: [] })),
      ),
      catchError((error) => {
        console.error('Error', error);
        return of(ProductsActions.loadProductsFailure({ error }));
      }),
    ),
  );

  // Luồng tạo sản phẩm mới
  createProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProductFormPageActions.submitForm),
      switchMap(({ payload }) =>
        this.productApi.createProduct(payload).pipe(
          map((response) =>
            ProductApiActions.createProductSuccess({ response }),
          ),
          catchError((error) =>
            of(ProductApiActions.createProductFailure({ error })),
          ),
        ),
      ),
    ),
  );
}
