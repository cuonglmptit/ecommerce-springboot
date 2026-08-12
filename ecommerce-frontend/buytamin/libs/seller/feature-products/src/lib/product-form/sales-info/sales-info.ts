import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  ControlContainer,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ProductFormData } from '../product-form-data';
import {
  AttributeApi,
  AttributeDefinitionFormGroup,
  AttributeInfo,
  AttributeOptionInfo,
  VariantAttributeInput,
  VariantFormGroup,
} from '@buytamin/seller/data-access';
import {
  debounceTime,
  distinctUntilChanged,
  of,
  Subject,
  switchMap,
} from 'rxjs';

@Component({
  selector: 'seller-sales-info',
  imports: [ReactiveFormsModule],
  templateUrl: './sales-info.html',
  styleUrl: './sales-info.scss',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class SalesInfo implements OnInit {
  private readonly formDataService = inject(ProductFormData);
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly attributeApi = inject(AttributeApi);

  private static readonly maxVariantsAllowed = 50;
  private static readonly maxAttributesAllowed = 2;

  readonly form = this.formDataService.form;
  readonly attributeDefsArray = this.formDataService.attributeDefsArray;
  readonly variantsArray = this.formDataService
    .variantsArray as FormArray<VariantFormGroup>;
  readonly maxVariantsAllowed = SalesInfo.maxVariantsAllowed;

  protected readonly attrDefsValue = toSignal(
    this.attributeDefsArray.valueChanges,
    { initialValue: this.attributeDefsArray.getRawValue() },
  );

  protected readonly hasVariants = toSignal(
    this.form.controls.hasVariants.valueChanges,
    { initialValue: this.form.controls.hasVariants.value },
  );

  protected readonly attribute1Name = computed(() => {
    const defs = this.attrDefsValue();
    return defs[0]?.name?.trim() || 'Variation 1';
  });

  protected readonly attribute2Name = computed(() => {
    const defs = this.attrDefsValue();
    return defs[1]?.name?.trim() || 'Variation 2';
  });

  protected readonly variation2OptionsLength = computed(() => {
    const defs = this.attrDefsValue();
    const options = defs[1]?.options;
    return options && options.length > 0 ? options.length : 1;
  });

  protected readonly totalVariantsCount = computed(() => {
    const defs = this.attrDefsValue();
    if (!defs || defs.length === 0) return 0;

    const counts = defs.map((group) => {
      const options = group.options || [];
      const len = options.length;
      const hasTrailing =
        len > 1 &&
        (!options[len - 1]?.name || options[len - 1]?.name?.trim() === '');
      return len > 1 && hasTrailing ? len - 1 : len || 1;
    });

    return counts.reduce((acc, count) => acc * count, 1);
  });

  protected attributeSuggestions = signal<AttributeInfo[][]>([[], []]);
  protected optionSuggestions = signal<
    Record<string, AttributeOptionInfo[]>
  >({});

  private readonly attrSearch$ = new Subject<{
    index: number;
    query: string;
  }>();
  private readonly optSearch$ = new Subject<{
    attrIndex: number;
    optIndex: number;
    query: string;
  }>();

  ngOnInit() {
    if (!this.hasVariants() && this.variantsArray.length === 0) {
      this.initSingleProductVariant();
    }

    this.attributeDefsArray.valueChanges.subscribe(() => {
      this.formDataService.buildMatrixFromDefinitions();
    });

    // RxJS Stream Search Attribute
    this.attrSearch$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(
          (a, b) => a.index === b.index && a.query === b.query,
        ),
        switchMap(({ index, query }) => {
          const shopId = this.formDataService.getSubmitPayload().shopId || 1;
          return this.attributeApi
            .searchAttributes(shopId, query)
            .pipe(switchMap((data) => of({ index, data })));
        }),
      )
      .subscribe(({ index, data }) => {
        const current = [...this.attributeSuggestions()];
        current[index] = data;
        this.attributeSuggestions.set(current);
      });

    // RxJS Stream Search Option
    this.optSearch$
      .pipe(
        debounceTime(300),
        switchMap(({ attrIndex, optIndex, query }) => {
          const attrName =
            this.attributeDefsArray.at(attrIndex)?.get('name')?.value || '';
          if (!attrName.trim())
            return of({ key: `${attrIndex}-${optIndex}`, data: [] });

          const shopId = this.formDataService.getSubmitPayload().shopId || 1;
          return this.attributeApi
            .searchOptions(shopId, attrName, query)
            .pipe(
              switchMap((data) =>
                of({ key: `${attrIndex}-${optIndex}`, data }),
              ),
            );
        }),
      )
      .subscribe(({ key, data }) => {
        this.optionSuggestions.update((prev) => ({ ...prev, [key]: data }));
      });
  }

  onAttributeNameInput(attrIndex: number, event: Event) {
    const query = (event.target as HTMLInputElement).value;
    this.attrSearch$.next({ index: attrIndex, query });

    const matched = this.attributeSuggestions()[attrIndex]?.find(
      (a) => a.name.toLowerCase() === query.trim().toLowerCase(),
    );

    const group = this.attributeDefsArray.at(attrIndex);
    if (group) {
      if (matched) {
        group.controls.id.setValue(matched.id);
      } else {
        group.controls.id.setValue(`temp-attr-${crypto.randomUUID()}`);
      }
    }
  }

  onOptionNameInput(attrIndex: number, optIndex: number, event: Event) {
    const query = (event.target as HTMLInputElement).value;
    this.optSearch$.next({ attrIndex, optIndex, query });
    this.syncTrailingEmptyInputs();

    const key = `${attrIndex}-${optIndex}`;
    const matched = this.optionSuggestions()[key]?.find(
      (o) => o.value.toLowerCase() === query.trim().toLowerCase(),
    );

    const targetGroup = this.attributeDefsArray.at(attrIndex);
    const optGroup = targetGroup?.controls.options.at(optIndex);

    if (optGroup) {
      if (matched) {
        optGroup.controls.id.setValue(matched.id);
      } else {
        optGroup.controls.id.setValue(`temp-opt-${crypto.randomUUID()}`);
      }
    }
  }

  enableVariants() {
    this.form.controls.hasVariants.setValue(true);
    this.variantsArray.clear();
    this.attributeDefsArray.clear();
    this.addAttributeDefinition();
  }

  disableVariants() {
    this.form.controls.hasVariants.setValue(false);
    this.attributeDefsArray.clear();
    this.initSingleProductVariant();
  }

  private initSingleProductVariant() {
    this.variantsArray.clear();

    const singleVariant: VariantFormGroup = this.fb.group({
      sku: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      salePrice: [0, [Validators.required, Validators.min(0)]],
      stockQuantity: [0, [Validators.required, Validators.min(0)]],
      attributes: this.fb.control<VariantAttributeInput[]>([]),
      optionNames: this.fb.control<string[]>([]),
    });

    this.variantsArray.push(singleVariant);
  }

  addAttributeDefinition() {
    if (this.attributeDefsArray.length >= SalesInfo.maxAttributesAllowed) {
      return;
    }

    const newAttributeGroup: AttributeDefinitionFormGroup = new FormGroup({
      id: new FormControl(`temp-attr-${crypto.randomUUID()}`, {
        nonNullable: true,
      }),
      name: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
      options: new FormArray<
        FormGroup<{ id: FormControl<string>; name: FormControl<string> }>
      >([]),
    });

    this.attributeDefsArray.push(newAttributeGroup);
    this.addOptionToAttribute(this.attributeDefsArray.length - 1);
  }

  addOptionToAttribute(attrIndex: number) {
    if (this.wouldExceedLimit(attrIndex)) {
      this.attributeDefsArray.controls.forEach((_, otherIndex) => {
        if (otherIndex !== attrIndex) {
          this.removeTrailingEmptyOption(otherIndex);
        }
      });
    }

    if (this.wouldExceedLimit(attrIndex)) {
      return;
    }

    const targetAttributeGroup = this.attributeDefsArray.at(attrIndex);
    if (!targetAttributeGroup) return;

    const optionsArray = targetAttributeGroup.controls.options;

    const newOption = new FormGroup({
      id: new FormControl(`temp-opt-${crypto.randomUUID()}`, {
        nonNullable: true,
      }),
      name: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required],
      }),
    });

    optionsArray.push(newOption);
  }

  private hasTrailingEmptyOption(attrIndex: number): boolean {
    const targetAttributeGroup = this.attributeDefsArray.at(attrIndex);
    if (!targetAttributeGroup) return false;

    const optionsArray = targetAttributeGroup.controls.options;
    if (optionsArray.length === 0) return false;

    const lastOptionGroup = optionsArray.at(optionsArray.length - 1);
    const lastOptionValue = lastOptionGroup?.get('name')?.value as
      | string
      | undefined;

    return !lastOptionValue || lastOptionValue.trim() === '';
  }

  private removeTrailingEmptyOption(attrIndex: number) {
    const targetAttributeGroup = this.attributeDefsArray.at(attrIndex);
    if (!targetAttributeGroup) return;

    const optionsArray = targetAttributeGroup.controls.options;
    if (optionsArray.length > 1 && this.hasTrailingEmptyOption(attrIndex)) {
      optionsArray.removeAt(optionsArray.length - 1);
    }
  }

  syncTrailingEmptyInputs() {
    this.attributeDefsArray.controls.forEach((_, attrIndex) => {
      if (this.isTrailingEmptyOptionIllegal(attrIndex)) {
        this.removeTrailingEmptyOption(attrIndex);
      }
    });

    this.attributeDefsArray.controls.forEach((attrGroup, attrIndex) => {
      const optionsArray = attrGroup.controls.options;

      optionsArray.controls.forEach((optCtrl, optIndex) => {
        const nameCtrl = optCtrl.controls.name;
        const isLastElement = optIndex === optionsArray.length - 1;
        const isNameEmpty = !nameCtrl.value || nameCtrl.value.trim() === '';

        if (isLastElement && isNameEmpty) {
          nameCtrl.clearValidators();
        } else {
          nameCtrl.setValidators([Validators.required]);
        }
        nameCtrl.updateValueAndValidity({ emitEvent: false });
      });

      if (optionsArray.length === 0) {
        this.addOptionToAttribute(attrIndex);
        return;
      }

      const lastIndex = optionsArray.length - 1;
      const lastOptionGroup = optionsArray.at(lastIndex);
      const lastOptionValue = lastOptionGroup?.get('name')?.value as
        | string
        | undefined;

      if (lastOptionValue && lastOptionValue.trim() !== '') {
        if (!this.wouldExceedLimit(attrIndex)) {
          this.addOptionToAttribute(attrIndex);
        }
      }
    });
  }

  removeOptionFromAttribute(attrIndex: number, optIndex: number) {
    const targetAttributeGroup = this.attributeDefsArray.at(attrIndex);
    if (!targetAttributeGroup) return;

    const optionsArray = targetAttributeGroup.controls.options;

    if (optionsArray.length > 1) {
      optionsArray.removeAt(optIndex);
      this.syncTrailingEmptyInputs();
    }
  }

  wouldExceedLimit(attrIndex: number): boolean {
    if (this.attributeDefsArray.length === 0) return false;
    const counts = this.attributeDefsArray.controls.map((_, index) => {
      const currentEffectiveLength = this.getEffectiveLength(index);
      return index === attrIndex
        ? currentEffectiveLength + 1
        : currentEffectiveLength;
    });
    const prospectiveTotal = counts.reduce((acc, count) => acc * count, 1);
    return prospectiveTotal > SalesInfo.maxVariantsAllowed;
  }

  private isTrailingEmptyOptionIllegal(attrIndex: number): boolean {
    const targetGroup = this.attributeDefsArray.at(attrIndex);
    if (!targetGroup) return false;

    const optionsArray = targetGroup.controls.options;
    const len = optionsArray.length;

    if (!this.hasTrailingEmptyOption(attrIndex) || len <= 1) {
      return false;
    }

    const counts = this.attributeDefsArray.controls.map((_, index) => {
      if (index === attrIndex) {
        return len;
      }
      return this.getEffectiveLength(index);
    });

    const prospectiveTotal = counts.reduce((acc, count) => acc * count, 1);
    return prospectiveTotal > SalesInfo.maxVariantsAllowed;
  }

  private getEffectiveLength(attrIndex: number): number {
    const group = this.attributeDefsArray.at(attrIndex);
    if (!group) return 1;

    const optionsArray = group.controls.options;
    const len = optionsArray.length;

    if (len > 1 && this.hasTrailingEmptyOption(attrIndex)) {
      return len - 1;
    }

    return len || 1;
  }

  applyBatchValues(price: string, stock: string, sku: string) {
    const numPrice = price ? Number(price) : null;
    const numStock = stock ? Number(stock) : null;
    const trimmedSku = sku ? sku.trim() : '';

    this.variantsArray.controls.forEach((variantCtrl) => {
      if (numPrice !== null && numPrice >= 0) {
        variantCtrl.controls.price.setValue(numPrice);
      }
      if (numStock !== null && numStock >= 0) {
        variantCtrl.controls.stockQuantity.setValue(numStock);
      }
      if (trimmedSku !== '') {
        variantCtrl.controls.sku.setValue(trimmedSku);
      }
    });
  }

  protected getAttributeSuggestions(index: number): AttributeInfo[] {
    return this.attributeSuggestions()[index] || [];
  }

  protected getOptionSuggestions(
    attrIndex: number,
    optIndex: number,
  ): AttributeOptionInfo[] {
    return this.optionSuggestions()[`${attrIndex}-${optIndex}`] || [];
  }
}
