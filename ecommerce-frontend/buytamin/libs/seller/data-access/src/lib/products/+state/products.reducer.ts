import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import { createReducer, on, createFeature } from '@ngrx/store';

import * as ProductsActions from './products.actions';
import { ProductsEntity } from '../products.models';
import { ProductApiActions, ProductFormPageActions } from './products.actions';

export interface ProductsState extends EntityState<ProductsEntity> {
  selectedId: string | number | null;
  loaded: boolean;
  error: any;
  isCreating: boolean;
  createSuccess: boolean;
}

export const productsAdapter: EntityAdapter<ProductsEntity> =
  createEntityAdapter<ProductsEntity>();

export const initialProductsState: ProductsState =
  productsAdapter.getInitialState({
    selectedId: null,
    loaded: false,
    error: null,
    isCreating: false,
    createSuccess: false,
  });

export const productsFeature = createFeature({
  name: 'products',
  reducer: createReducer(
    initialProductsState,
    on(ProductsActions.initProducts, (state) => ({
      ...state,
      loaded: false,
      error: null,
    })),
    on(ProductsActions.loadProductsSuccess, (state, { products }) =>
      productsAdapter.setAll(products, { ...state, loaded: true }),
    ),
    on(ProductsActions.loadProductsFailure, (state, { error }) => ({
      ...state,
      error,
    })),

    // Luồng xử lý Form Tạo mới
    on(ProductFormPageActions.submitForm, (state) => ({
      ...state,
      isCreating: true,
      createSuccess: false,
      error: null,
    })),
    on(ProductApiActions.createProductSuccess, (state, { response }) =>
      productsAdapter.addOne(response, {
        ...state,
        isCreating: false,
        createSuccess: true,
      }),
    ),
    on(ProductApiActions.createProductFailure, (state, { error }) => ({
      ...state,
      isCreating: false,
      error,
    })),
  ),
});
