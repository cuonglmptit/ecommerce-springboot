import { FormArray, FormControl, FormGroup } from '@angular/forms';

export type VariantFormGroup = FormGroup<{
  sku: FormControl<string>;
  price: FormControl<number>;
  salePrice: FormControl<number>;
  stockQuantity: FormControl<number>;
  attributeOptionIds: FormArray<FormControl<string>>;
  optionNames: FormControl<string[]>;
}>;

export type AttributeDefinitionFormGroup = FormGroup<{
  id: FormControl<string>;
  name: FormControl<string>;
  options: FormArray<
    FormGroup<{ id: FormControl<string>; name: FormControl<string> }>
  >;
}>;
