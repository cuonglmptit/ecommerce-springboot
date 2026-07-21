import { createAction, createActionGroup, props } from '@ngrx/store';
import { CreateProductRequest, ProductsEntity } from '../products.models';

export const initProducts = createAction('[Products Page] Init');

export const loadProductsSuccess = createAction(
  '[Products/API] Load Products Success',
  props<{ products: ProductsEntity[] }>(),
);

export const loadProductsFailure = createAction(
  '[Products/API] Load Products Failure',
  props<{ error: any }>(),
);


// Actions cho phần Product Form
export const ProductFormPageActions = createActionGroup(
  {
  source: 'Product Form Page',
  events: {
    'Submit Form': props<{ payload: CreateProductRequest }>(),
  },
});

export const ProductApiActions = createActionGroup({
  source: 'Product API',
  events: {
    'Create Product Success': props<{ response: any }>(),
    'Create Product Failure': props<{ error: any }>(),
  },
});
