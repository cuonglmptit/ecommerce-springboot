import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { VariantAttributeInput } from './products.models';

export type VariantFormGroup = FormGroup<{
  sku: FormControl<string>;
  price: FormControl<number>;
  salePrice: FormControl<number>;
  stockQuantity: FormControl<number>;
  attributes: FormControl<VariantAttributeInput[]>;
  optionNames: FormControl<string[]>;
}>;

export type AttributeDefinitionFormGroup = FormGroup<{
  id: FormControl<string>;
  name: FormControl<string>;
  options: FormArray<
    FormGroup<{ id: FormControl<string>; name: FormControl<string> }>
  >;
}>;
