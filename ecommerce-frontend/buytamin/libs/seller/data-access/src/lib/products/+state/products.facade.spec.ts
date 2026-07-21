import { NgModule } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule, Store } from '@ngrx/store';
import { firstValueFrom } from 'rxjs';

import * as ProductsActions from './products.actions';
import { ProductsEffects } from './products.effects';
import { ProductsFacade } from './products.facade.ts.bak';
import { ProductsEntity } from '../products.models';
import {
  PRODUCTS_FEATURE_KEY,
  ProductsState,
  initialProductsState,
  productsReducer,
} from './products.reducer';
import * as ProductsSelectors from './products.selectors.ts.bak';

interface TestSchema {
  products: ProductsState;
}

describe('ProductsFacade', () => {
  let facade: ProductsFacade;
  let store: Store<TestSchema>;
  const createProductsEntity = (id: string, name = ''): ProductsEntity => ({
    id,
    name: name || `name-${id}`,
  });

  describe('used in NgModule', () => {
    beforeEach(() => {
      @NgModule({
        imports: [
          StoreModule.forFeature(PRODUCTS_FEATURE_KEY, productsReducer),
          EffectsModule.forFeature([ProductsEffects]),
        ],
        providers: [ProductsFacade],
      })
      class CustomFeatureModule {}

      @NgModule({
        imports: [
          StoreModule.forRoot({}),
          EffectsModule.forRoot([]),
          CustomFeatureModule,
        ],
      })
      class RootModule {}
      TestBed.configureTestingModule({ imports: [RootModule] });

      store = TestBed.inject(Store);
      facade = TestBed.inject(ProductsFacade);
    });

    /**
     * The initially generated facade::loadAll() returns empty array
     */
    it('loadAll() should return empty list with loaded == true', async () => {
      let list = await firstValueFrom(facade.allProducts$);
      let isLoaded = await firstValueFrom(facade.loaded$);

      expect(list.length).toBe(0);
      expect(isLoaded).toBe(false);

      facade.init();

      list = await firstValueFrom(facade.allProducts$);
      isLoaded = await firstValueFrom(facade.loaded$);

      expect(list.length).toBe(0);
      expect(isLoaded).toBe(true);
    });

    /**
     * Use `loadProductsSuccess` to manually update list
     */
    it('allProducts$ should return the loaded list; and loaded flag == true', async () => {
      let list = await firstValueFrom(facade.allProducts$);
      let isLoaded = await firstValueFrom(facade.loaded$);

      expect(list.length).toBe(0);
      expect(isLoaded).toBe(false);

      store.dispatch(
        ProductsActions.loadProductsSuccess({
          products: [createProductsEntity('AAA'), createProductsEntity('BBB')],
        }),
      );

      list = await firstValueFrom(facade.allProducts$);
      isLoaded = await firstValueFrom(facade.loaded$);

      expect(list.length).toBe(2);
      expect(isLoaded).toBe(true);
    });
  });
});
