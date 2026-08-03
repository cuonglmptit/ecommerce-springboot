import { inject, Injectable } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  Validators,
} from '@angular/forms';
import {
  AttributeDefinitionFormGroup,
  CreateProductRequest,
  VariantAttributeInput,
  VariantFormGroup,
} from '@buytamin/seller/data-access';

@Injectable()
export class ProductFormData {
  private readonly fb = inject(FormBuilder).nonNullable;

  readonly form = this.fb.group({
    basicInfo: this.fb.group({
      name: ['', Validators.required],
      categoryId: [1, Validators.required],
    }),
    description: ['', Validators.required],
    hasVariants: [false],
    attributeDefinitions: new FormArray<AttributeDefinitionFormGroup>([]),
    variants: this.fb.array<VariantFormGroup>([]),
  });

  get variantsArray(): FormArray<VariantFormGroup> {
    return this.form.controls.variants;
  }

  get attributeDefsArray(): FormArray<AttributeDefinitionFormGroup> {
    return this.form.controls.attributeDefinitions;
  }

  constructor() {
    this.form.controls.hasVariants.valueChanges.subscribe((hasVariants) => {
      if (!hasVariants) {
        this.variantsArray.clear();
      }
    });
  }

  buildMatrixFromDefinitions() {
    const definitions = this.attributeDefsArray.getRawValue();

    // Lọc bỏ các nhóm thuộc tính trống không có tùy chọn nào
    const validGroups = definitions
      .map((group) => {
        if (!group.options || group.options.length === 0) {
          return { ...group, options: [] };
        }

        const lastIndex = group.options.length - 1;
        const lastOption = group.options[lastIndex];
        const isTrailingEmpty =
          !lastOption.name || lastOption.name.trim() === '';

        return {
          ...group,
          options: isTrailingEmpty
            ? group.options.slice(0, lastIndex)
            : group.options,
        };
      })
      .filter((g) => g.options && g.options.length > 0);

    if (validGroups.length === 0) {
      this.variantsArray.clear();
      return;
    }

    const combinations = validGroups.reduce((acc, group) => {
      if (acc.length === 0) {
        return group.options.map((opt) => [
          { attrId: group.id, attrName: group.name, ...opt },
        ]);
      }
      return acc.flatMap((combo) =>
        group.options.map((opt) => [
          ...combo,
          { attrId: group.id, attrName: group.name, ...opt },
        ]),
      );
    }, [] as any[][]);

    const formattedCombinations = combinations.map((combo) => {
      const skuString =
        'SKU-' + combo.map((o) => o.name.toUpperCase()).join('-');

      const attributes: VariantAttributeInput[] = combo.map((o) => ({
        attributeId:
          o.attrId.startsWith('temp-') || o.attrId.length < 30
            ? null
            : o.attrId,
        attributeName: o.attrName,
        optionId: o.id.startsWith('temp-') || o.id.length < 30 ? null : o.id,
        optionValue: o.name,
      }));

      return {
        sku: skuString,
        attributes,
        optionNames: combo.map((o) => o.name),
      };
    });

    this.generateVariants(formattedCombinations);
  }

  generateVariants(
    combinations: {
      sku: string;
      attributes: VariantAttributeInput[];
      optionNames: string[];
    }[],
  ) {
    this.variantsArray.clear();

    combinations.forEach((combo) => {
      const variantGroup: VariantFormGroup = this.fb.group({
        sku: [combo.sku, Validators.required],
        price: [150000, [Validators.required, Validators.min(0)]],
        salePrice: [120000, [Validators.required, Validators.min(0)]],
        stockQuantity: [100, [Validators.required, Validators.min(0)]],
        attributes: this.fb.control(combo.attributes),
        optionNames: this.fb.control(combo.optionNames),
      });

      this.variantsArray.push(variantGroup);
    });
  }

  getSubmitPayload(): CreateProductRequest {
    const rawValue = this.form.getRawValue();

    return {
      shopId: 2,
      categoryId: rawValue.basicInfo.categoryId,
      name: rawValue.basicInfo.name,
      description: rawValue.description,
      imageUrl: 'https://dummyimage.com/600x400',
      publicPrice: 0,
      productMedia: [
        {
          mediaId: '00000000-0000-0000-0000-000000000001',
          isThumbnail: true,
          sortOrder: 0,
        },
      ],
      variants: rawValue.variants,
    };
  }
}
