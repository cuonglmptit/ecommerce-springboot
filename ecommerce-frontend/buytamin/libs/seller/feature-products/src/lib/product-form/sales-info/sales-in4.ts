import { Component, inject, OnInit } from '@angular/core';
import { ProductFormData } from '../product-form-data';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'seller-sales-in4',
  templateUrl: 'sales-in4.html',
  styleUrls: ['sales-in4.scss'],
  imports: [ReactiveFormsModule],
})
export class SalesIn4 implements OnInit {
  private readonly formDataService = inject(ProductFormData);
  readonly ccc = new FormControl("kkkk")
  readonly form = this.formDataService.form;
  readonly variantsArray = this.formDataService.variantsArray;
  private readonly fb = inject(FormBuilder).nonNullable;

  private static readonly MAX_ATTRIBUTES_ALLOWED = 2;
  private static readonly MAX_VARIANTS_ALLOWED = 50;

  protected readonly hasVariants = toSignal(
    this.form.controls.hasVariants.valueChanges,
    { initialValue: this.form.controls.hasVariants.value },
  );

  enableVariants() {
    this.form.controls.hasVariants.setValue(true);
  }

  ngOnInit() {
    // if(!this.hasVariants() && this.form.controls.variants.length == 0) {
    //   this.initSingleProductVariant()
    // }
    console.log('Giá trị ban đầu của ccc:', this.ccc.value);
  }
}
