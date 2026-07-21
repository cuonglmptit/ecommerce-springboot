import { Component, inject } from '@angular/core';
import { ProductFormData } from '../product-form-data';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'seller-product-description',
  imports: [ReactiveFormsModule],
  templateUrl: './product-description.html',
  styleUrl: './product-description.scss',
})
export class ProductDescription {
  readonly description = inject(ProductFormData).form.controls.description;
}
