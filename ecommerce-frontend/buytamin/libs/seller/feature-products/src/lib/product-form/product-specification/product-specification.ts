import { Component } from '@angular/core';
import {
  ControlContainer,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';

@Component({
  selector: 'seller-product-specification',
  imports: [ReactiveFormsModule],
  templateUrl: './product-specification.html',
  styleUrl: './product-specification.scss',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective }, // ◄── Copy dòng này qua toàn bộ các con còn lại
  ],
})
export class ProductSpecification {}
