import { Component, inject } from '@angular/core';
import { BasicInfo } from './basic-info/basic-info';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProductFormData } from './product-form-data';
import { Store } from '@ngrx/store';
import {
  ProductFormPageActions,
  productsFeature,
} from '@buytamin/seller/data-access';

import { ProductDescription } from './product-description/product-description';
import { ProductSpecification } from './product-specification/product-specification';
import { SalesInfo } from './sales-info/sales-info';
import { Shipping } from './shipping/shipping';

@Component({
  selector: 'seller-product-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    BasicInfo,
    ProductDescription,
    FormsModule,
    ProductSpecification,
    SalesInfo,
    Shipping,
  ],
  templateUrl: './product-form.html',
  styleUrl: './product-form.scss',
  providers: [ProductFormData],
})
export class ProductForm {
  private readonly formData = inject(ProductFormData);
  private readonly store = inject(Store);

  protected readonly isCreating = this.store.selectSignal(
    productsFeature.selectIsCreating,
  );

  onSubmit() {
    if (this.formData.form.invalid) {
      this.formData.form.markAllAsTouched();
      return;
    }

    const payload = this.formData.getSubmitPayload();
    this.store.dispatch(ProductFormPageActions.submitForm({ payload }));
    console.log(payload);
  }
}
