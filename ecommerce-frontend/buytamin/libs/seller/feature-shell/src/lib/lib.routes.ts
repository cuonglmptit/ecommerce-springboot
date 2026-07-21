import { Route } from '@angular/router';
import { FullWidthLayout } from './layout/full-width-layout/full-width-layout';
import { SidebarLayout } from './layout/sidebar-layout/sidebar-layout';
import { provideState } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import {
  productsFeature,
  ProductsEffects,
} from '@buytamin/seller/data-access';
export const sellerFeatureShell: Route[] = [
  {
    path: '',
    component: SidebarLayout,
    children: [
      {
        path: 'products', // Trang danh sách sản phẩm (có sidebar)
        loadComponent: () =>
          import('@buytamin/seller/feature-products').then(
            (m) => m.SellerFeatureProducts,
          ),
      },
    ],
  },
  {
    path: 'products',
    component: FullWidthLayout,
    providers: [
      provideState(productsFeature),
      provideEffects(ProductsEffects),
    ],
    children: [
      {
        path: 'new', // URL: /products/new
        loadComponent: () =>
          import('@buytamin/seller/feature-products').then(
            (m) => m.ProductForm,
          ),
        data: { mode: 'new' },
      },
      {
        path: 'edit/:id', // URL: /products/edit/:id
        loadComponent: () =>
          import('@buytamin/seller/feature-products').then(
            (m) => m.ProductForm,
          ),
        data: { mode: 'edit' },
      },
    ],
  },
];
