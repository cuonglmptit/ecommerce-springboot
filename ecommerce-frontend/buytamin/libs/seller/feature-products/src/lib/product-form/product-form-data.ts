import { inject, Injectable } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  Validators,
} from '@angular/forms';
import {
  AttributeDefinitionFormGroup,
  CreateProductRequest,
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

    // Thuật toán tích Descartes tính toán các tổ hợp option
    const combinations = validGroups.reduce((acc, group) => {
      if (acc.length === 0) {
        return group.options.map((opt) => [opt]);
      }
      return acc.flatMap((combo) =>
        group.options.map((opt) => [...combo, opt]),
      );
    }, [] as any[][]);

    // Convert sang cấu trúc dữ liệu để sinh Form
    const formattedCombinations = combinations.map((combo) => {
      // combo sẽ có dạng: [ {id: 'uuid-do', name: 'do'}, {id: 'uuid-xx', name: 'xx'} ]
      const skuString = combo.map((o) => o.name.toUpperCase()).join('-'); // Tự sinh SKU nháp
      return {
        sku: skuString,
        optionIds: combo.map((o) => o.id),
        optionNames: combo.map((o) => o.name), // Giữ lại cái tên để hiển thị lên bảng
      };
    });

    this.generateVariants(formattedCombinations);
  }

  generateVariants(
    combinations: { sku: string; optionIds: string[]; optionNames: string[] }[],
  ) {
    this.variantsArray.clear();

    combinations.forEach((combo) => {
      // Khởi tạo cụm FormGroup bọc chuẩn kiểu dữ liệu của VariantFormGroup
      const variantGroup: VariantFormGroup = this.fb.group({
        sku: [combo.sku, Validators.required],
        price: [0, [Validators.required, Validators.min(0)]],
        salePrice: [0, [Validators.required, Validators.min(0)]],
        stockQuantity: [0, [Validators.required, Validators.min(0)]],
        // Biến đổi mảng string[] thành FormArray các FormControl<string>
        attributeOptionIds: this.fb.array(
          combo.optionIds.map((id) => this.fb.control(id)),
        ),
        optionNames: this.fb.control(combo.optionNames),
      });

      // Push vào mảng an toàn vì đã trùng khớp kiểu dữ liệu
      this.variantsArray.push(variantGroup);
    });
  }
  getSubmitPayload(): CreateProductRequest {
    const rawValue = this.form.getRawValue();

    return {
      shopId: 1,
      categoryId: rawValue.basicInfo.categoryId,
      name: rawValue.basicInfo.name,
      description: rawValue.description,

      // Bổ sung 2 trường dummy bị thiếu do kế thừa từ BaseProduct
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
