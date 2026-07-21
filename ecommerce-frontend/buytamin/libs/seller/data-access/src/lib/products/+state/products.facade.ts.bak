import { Injectable, inject } from '@angular/core';
import { select, Store, Action } from '@ngrx/store';

import * as ProductsActions from './products.actions';
import * as ProductsFeature from './products.reducer';
import * as ProductsSelectors from './products.selectors';

@Injectable()
export class ProductsFacade {
  private readonly store = inject(Store);

  /**
   * Combine pieces of state using createSelector,
   * and expose them as observables through the facade.
   */
  loaded$ = this.store.pipe(select(ProductsSelectors.selectProductsLoaded));
  allProducts$ = this.store.pipe(select(ProductsSelectors.selectAllProducts));
  selectedProducts$ = this.store.pipe(select(ProductsSelectors.selectEntity));

  /**
   * Use the initialization action to perform one
   * or more tasks in your Effects.
   */
  init() {
    this.store.dispatch(ProductsActions.initProducts());
  }
}
