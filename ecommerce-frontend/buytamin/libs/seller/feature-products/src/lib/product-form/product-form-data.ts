import { computed, inject, Injectable, signal } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import {
  AttributeDefinitionFormGroup,
  CreateProductRequest,
  ProductMedia,
  ProductSpecificationInput,
  ShopApi,
  VariantAttributeInput,
  VariantFormGroup,
} from '@buytamin/seller/data-access';
import { toSignal } from '@angular/core/rxjs-interop';

@Injectable()
export class ProductFormData {
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly shopApi = inject(ShopApi);

  static readonly MAX_VARIANTS_ALLOWED = 50;
  static readonly MAX_ATTRIBUTES_ALLOWED = 2;

  readonly currentShopId = signal<number | null>(null);
  readonly productMediaList = signal<ProductMedia[]>([]);
  readonly specificationsList = signal<ProductSpecificationInput[]>([]);

  readonly form = this.fb.group({
    basicInfo: this.fb.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(255),
        ],
      ],
      categoryId: this.fb.control<number | null>(null, [Validators.required]),
    }),
    description: ['', [Validators.required, Validators.minLength(10)]],
    hasVariants: [false],
    attributeDefinitions: new FormArray<AttributeDefinitionFormGroup>([]),
    variants: this.fb.array<VariantFormGroup>([]),
  });

  readonly formStatus = toSignal(
    this.form.statusChanges,
    { initialValue: this.form.status },
  );

  readonly isReadyToSubmit = computed(() => {
    const formValid = this.formStatus() === 'VALID';
    const hasMedia = this.productMediaList().length > 0;

    return formValid && hasMedia;
  });

  get variantsArray(): FormArray<VariantFormGroup> {
    return this.form.controls.variants;
  }

  get attributeDefsArray(): FormArray<AttributeDefinitionFormGroup> {
    return this.form.controls.attributeDefinitions;
  }

  constructor() {
    this.loadCurrentShop();

    this.form.controls.hasVariants.valueChanges.subscribe((hasVariants) => {
      if (!hasVariants) {
        this.variantsArray.clear();
      }
    });
  }

  private loadCurrentShop(): void {
    this.shopApi.getMyShop().subscribe({
      next: (shop) => this.currentShopId.set(shop.id),
      error: (err) => console.error('Không thể tải thông tin Shop:', err),
    });
  }

  addMedia(item: ProductMedia): void {
    this.productMediaList.update((list) => [...list, item]);
  }

  removeMedia(index: number): void {
    this.productMediaList.update((list) => {
      const updated = list.filter((_, idx) => idx !== index);
      if (updated.length > 0 && !updated.some((m) => m.isThumbnail)) {
        updated[0] = { ...updated[0], isThumbnail: true };
      }
      return updated.map((m, idx) => ({ ...m, sortOrder: idx }));
    });
  }

  setThumbnailIndex(targetIndex: number): void {
    this.productMediaList.update((list) =>
      list.map((m, idx) => ({
        ...m,
        isThumbnail: idx === targetIndex,
      })),
    );
  }

  buildMatrixFromDefinitions(): void {
    const definitions = this.attributeDefsArray.getRawValue();

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
  ): void {
    this.variantsArray.clear();

    combinations.forEach((combo) => {
      const variantGroup: VariantFormGroup = this.fb.group({
        sku: [combo.sku, Validators.required],
        price: [150000, [Validators.required, Validators.min(0)]],
        salePrice: [120000, [Validators.required, Validators.min(0)]],
        stockQuantity: [100, [Validators.required, Validators.min(0)]],
        attributes: this.fb.control(combo.attributes),
        optionNames: this.fb.control(combo.optionNames),
        media: this.fb.control<ProductMedia[]>([]),
      });

      this.variantsArray.push(variantGroup);
    });
  }

  getSubmitPayload(): CreateProductRequest {
    const rawValue = this.form.getRawValue();
    const shopId = this.currentShopId();

    if (!shopId) {
      throw new Error('Chưa tải được thông tin Shop. Vui lòng thử lại!');
    }

    const media = this.productMediaList();
    if (media.length === 0) {
      throw new Error('Vui lòng tải lên ít nhất 1 hình ảnh sản phẩm.');
    }

    return {
      shopId: shopId,
      categoryId: rawValue.basicInfo.categoryId,
      name: rawValue.basicInfo.name.trim(),
      description: rawValue.description.trim(),
      specifications: this.specificationsList(), // Gom specs động đầy đủ
      media: media,
      variants: rawValue.variants,
    };
  }
}
