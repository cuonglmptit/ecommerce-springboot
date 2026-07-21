import { Route } from '@angular/router';

export const appRoutes: Route[] = [
  {
    path: '',
    loadChildren: () =>
      import('@buytamin/seller/feature-shell').then(
        (m) => m.sellerFeatureShell,
      ),
  },
  {
    path: '**',
    loadComponent: () => import('@buytamin/shared/ui-common').then((m) => m.FullPageError),
  },
];
