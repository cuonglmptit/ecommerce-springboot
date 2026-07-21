import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ProductFormData } from '../product-form-data';

@Component({
  selector: 'seller-basic-info',
  imports: [ReactiveFormsModule],
  templateUrl: './basic-info.html',
  styleUrl: './basic-info.scss',
})
export class BasicInfo {
  protected readonly basicInfoGroup =
    inject(ProductFormData).form.controls.basicInfo;
}
