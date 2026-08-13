import { Component, input } from '@angular/core';
import {
  ControlContainer,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { CategoryAttributeInfo } from '@buytamin/seller/data-access';

@Component({
  selector: 'seller-product-specification',
  imports: [ReactiveFormsModule],
  templateUrl: './product-specification.html',
  styleUrl: './product-specification.scss',
})
export class ProductSpecification {
  attributes = input<CategoryAttributeInfo[]>([]);
}
