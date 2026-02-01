import { Route } from '@angular/router';

export const appRoutes: Route[] = [
  {
    path: 'seller',
    loadComponent: () =>
      import('@buytamin/seller/feature-shell').then(
        (m) => m.SellerFeatureShell,
      ),
  },
];
