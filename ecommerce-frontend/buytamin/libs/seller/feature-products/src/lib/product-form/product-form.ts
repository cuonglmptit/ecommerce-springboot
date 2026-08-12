import { Component, inject, signal } from '@angular/core';
import { BasicInfo } from './basic-info/basic-info';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProductFormData } from './product-form-data';
import { Store } from '@ngrx/store';
import {
  CategoryApi,
  CategoryAttributeInfo,
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
  private readonly categoryApi = inject(CategoryApi);
  protected readonly specAttributes = signal<CategoryAttributeInfo[]>([]);

  protected readonly isCreating = this.store.selectSignal(
    productsFeature.selectIsCreating,
  );

  onCategoryChanged(categoryId: number): void {
    // Gọi API lấy thông số kỹ thuật (SPECIFICATION) của danh mục
    this.categoryApi
      .getCategoryAttributes(categoryId, 'SPECIFICATION')
      .subscribe({
        next: (res) => {
          if (res.data) {
            // Gán vào Signal ➔ ProductSpecification tự động nhận qua input()
            this.specAttributes.set(res.data);
          }
        },
      });
  }

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
