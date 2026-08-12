// 1. Export Feature (chứa Reducer) để file routing (shell) cấu hình provideState
export { productsFeature } from './lib/products/+state/products.reducer';

export { ProductsEffects } from './lib/products/+state/products.effects';
export * from './lib/products/+state/products.actions';
export * from './lib/products/products.models';
export * from './lib/products/product-form.model';

export * from './lib/attributes/attribute-api';
export * from './lib/attributes/attribute.models';
export * from './lib/categories/category.models';
export * from './lib/categories/category-api';
