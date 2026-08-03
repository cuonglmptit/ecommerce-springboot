import { Component, inject, OnInit } from '@angular/core';
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
  AttributeInfoDTO,
  AttributeOptionInfoDTO,
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

  // Stream danh sách gợi ý cho từng ô
  protected attributeSuggestions: AttributeInfoDTO[][] = [[], []];
  protected optionSuggestions: Record<string, AttributeOptionInfoDTO[]> = {};

  private readonly attrSearch$ = new Subject<{
    index: number;
    query: string;
  }>();
  private readonly optSearch$ = new Subject<{
    attrIndex: number;
    optIndex: number;
    query: string;
  }>();

  get form() {
    return this.formDataService.form;
  }

  get attributeDefsArray() {
    return this.formDataService.attributeDefsArray;
  }

  get variantsArray(): FormArray<VariantFormGroup> {
    return this.formDataService.variantsArray as FormArray<VariantFormGroup>;
  }

  get maxVariantsAllowed() {
    return SalesInfo.maxVariantsAllowed;
  }

  ngOnInit() {
    if (
      !this.form.controls.hasVariants.value &&
      this.variantsArray.length === 0
    ) {
      this.initSingleProductVariant();
    }

    this.attributeDefsArray.valueChanges.subscribe(() => {
      this.formDataService.buildMatrixFromDefinitions();
    });

    // (Debounce 300ms + Auto Cancel Request cũ bằng switchMap)
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
        this.attributeSuggestions[index] = data;
      });


    // Subscribes RxJS tìm kiếm Option
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
        this.optionSuggestions[key] = data;
      });
  }

  // Hàm lắng nghe khi gõ Tên Thuộc tính
  onAttributeNameInput(attrIndex: number, event: Event) {
    const query = (event.target as HTMLInputElement).value;
    this.attrSearch$.next({ index: attrIndex, query });

    // Kiểm tra xem chữ gõ vào có trùng với ID có sẵn trong gợi ý không
    const matched = this.attributeSuggestions[attrIndex]?.find(
      (a) => a.name.toLowerCase() === query.trim().toLowerCase(),
    );

    const group = this.attributeDefsArray.at(attrIndex);
    if (group) {
      if (matched) {
        group.controls.id.setValue(matched.id); // Chọn đúng ID từ DB
      } else {
        group.controls.id.setValue(`temp-attr-${crypto.randomUUID()}`); // Gõ từ mới -> Gán ID tạm
      }
    }
  }

  // Hàm lắng nghe khi gõ Tên Phân loại
  onOptionNameInput(attrIndex: number, optIndex: number, event: Event) {
    const query = (event.target as HTMLInputElement).value;
    this.optSearch$.next({ attrIndex, optIndex, query });
    this.syncTrailingEmptyInputs();

    const key = `${attrIndex}-${optIndex}`;
    const matched = this.optionSuggestions[key]?.find(
      (o) => o.value.toLowerCase() === query.trim().toLowerCase(),
    );

    const targetGroup = this.attributeDefsArray.at(attrIndex);
    const optGroup = targetGroup?.controls.options.at(optIndex);

    if (optGroup) {
      if (matched) {
        optGroup.controls.id.setValue(matched.id); // Chọn đúng ID từ DB
      } else {
        optGroup.controls.id.setValue(`temp-opt-${crypto.randomUUID()}`); // Gõ từ mới -> Gán ID tạm
      }
    }
  }

  get hasVariants(): boolean {
    return this.form.controls.hasVariants.value;
  }

  enableVariants() {
    this.form.controls.hasVariants.setValue(true);
    this.variantsArray.clear();
    this.attributeDefsArray.clear();
    // Thêm 1 nhóm thuộc tính trống ban đầu
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

    // Tự động sinh sẵn 1 ô tùy chọn trống ban đầu cho người dùng dễ nhập liệu
    this.addOptionToAttribute(this.attributeDefsArray.length - 1);
  }

  //  Hàm sinh ô tùy chọn con (Màu/Size...) vào đúng nhóm thuộc tính được định định
  addOptionToAttribute(attrIndex: number) {
    if (this.wouldExceedLimit(attrIndex)) {
      this.attributeDefsArray.controls.forEach((_, otherIndex) => {
        if (otherIndex !== attrIndex) {
          this.removeTrailingEmptyOption(otherIndex);
        }
      });
    }

    // Kiểm tra lại sau khi dọn dẹp, nếu vẫn vượt quá thì mới chịu chặn cứng
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

  // Thực hiện xóa ô trống cuối cùng của nhóm nếu nhóm đó có nhiều hơn 1 phân loại
  private removeTrailingEmptyOption(attrIndex: number) {
    const targetAttributeGroup = this.attributeDefsArray.at(attrIndex);
    if (!targetAttributeGroup) return;

    const optionsArray = targetAttributeGroup.controls.options;
    // Chỉ được xóa nếu mảng vẫn còn tối thiểu 1 phần tử để giữ cấu trúc Form
    if (optionsArray.length > 1 && this.hasTrailingEmptyOption(attrIndex)) {
      optionsArray.removeAt(optionsArray.length - 1);
    }
  }

  syncTrailingEmptyInputs() {
    // Bước 1: Quét và XÓA tất cả các ô trống chờ đã trở nên "bất hợp pháp" do nhóm khác tăng số lượng
    this.attributeDefsArray.controls.forEach((_, attrIndex) => {
      if (this.isTrailingEmptyOptionIllegal(attrIndex)) {
        this.removeTrailingEmptyOption(attrIndex);
      }
    });

    // Bước 2: Quét và THÊM ô trống mới cho các nhóm đã điền đầy đủ (nếu không vượt quá giới hạn)
    this.attributeDefsArray.controls.forEach((attrGroup, attrIndex) => {
      const optionsArray = attrGroup.controls.options;

      optionsArray.controls.forEach((optCtrl, optIndex) => {
        const nameCtrl = optCtrl.controls.name;
        const isLastElement = optIndex === optionsArray.length - 1;
        const isNameEmpty = !nameCtrl.value || nameCtrl.value.trim() === '';

        // Ô cuối cùng và đang trống -> Gỡ validator
        if (isLastElement && isNameEmpty) {
          nameCtrl.clearValidators();
        } else {
          // Ô ở giữa (hoặc ô cuối đã điền chữ) -> Ép validator required
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

      // Nếu ô cuối cùng đã có chữ (không trống)
      if (lastOptionValue && lastOptionValue.trim() !== '') {
        // Và nếu thêm 1 ô nữa cho nhóm này không làm vượt quá giới hạn
        if (!this.wouldExceedLimit(attrIndex)) {
          this.addOptionToAttribute(attrIndex); // Tự động bù thêm ô trống ở cuối
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

  // Tính tổng số biến thể dựa trên số lượng thực dụng
  get totalVariantsCount(): number {
    if (this.attributeDefsArray.length === 0) return 0;

    const counts = this.attributeDefsArray.controls.map((_, index) =>
      this.getEffectiveLength(index),
    );

    // Nhân tất cả các số lượng thực dụng lại với nhau
    return counts.reduce((acc, count) => acc * count, 1);
  }

  // 2. Hàm dự báo trước: "Nếu tôi thêm 1 tùy chọn vào Nhóm này, liệu tổng số biến thể có vượt quá MAX không?"
  // Dự báo giới hạn dựa trên số lượng thực dụng
  wouldExceedLimit(attrIndex: number): boolean {
    if (this.attributeDefsArray.length === 0) return false;

    // Giả lập tăng số lượng tùy chọn thực tế của riêng nhóm hiện tại lên 1 đơn vị
    const counts = this.attributeDefsArray.controls.map((_, index) => {
      const currentEffectiveLength = this.getEffectiveLength(index);
      return index === attrIndex
        ? currentEffectiveLength + 1
        : currentEffectiveLength;
    });

    // Tính thử tích số mới sau khi cộng giả lập
    const prospectiveTotal = counts.reduce((acc, count) => acc * count, 1);

    return prospectiveTotal > SalesInfo.maxVariantsAllowed;
  }
  private isTrailingEmptyOptionIllegal(attrIndex: number): boolean {
    const targetGroup = this.attributeDefsArray.at(attrIndex);
    if (!targetGroup) return false;

    const optionsArray = targetGroup.controls.options;
    const len = optionsArray.length;

    // Nếu không có ô trống chờ cuối cùng, hoặc nhóm chỉ có đúng 1 ô (bắt buộc giữ) thì bỏ qua
    if (!this.hasTrailingEmptyOption(attrIndex) || len <= 1) {
      return false;
    }

    // Giả lập kịch bản: Nếu người dùng ĐIỀN CHỮ vào ô trống này, tổng biến thể sẽ là bao nhiêu?
    const counts = this.attributeDefsArray.controls.map((_, index) => {
      if (index === attrIndex) {
        return len; // Ô trống này nếu điền chữ thì số lượng thực dụng của nhóm sẽ bằng len
      }
      return this.getEffectiveLength(index);
    });

    const prospectiveTotal = counts.reduce((acc, count) => acc * count, 1);

    // Nếu việc điền chữ vào đây làm vượt ngưỡng tối đa, ô trống này chính là "bất hợp pháp" và cần xóa ngay!
    return prospectiveTotal > SalesInfo.maxVariantsAllowed;
  }
  private getEffectiveLength(attrIndex: number): number {
    const group = this.attributeDefsArray.at(attrIndex);
    if (!group) return 1;

    const optionsArray = group.controls.options;
    const len = optionsArray.length;

    // Chỉ trừ đi 1 nếu nhóm có từ 2 ô trở lên và ô cuối cùng thực sự trống
    if (len > 1 && this.hasTrailingEmptyOption(attrIndex)) {
      return len - 1;
    }

    return len || 1; // Mặc định là 1 nếu nhóm chưa có gì để không làm hỏng phép nhân dồn
  }

  //  THÊM MỚI: Hàm xử lý áp dụng giá trị hàng loạt (Apply To All)
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

  // --- Logic tính toán hiển thị bảng ma trận ---
  get variation2OptionsLength(): number {
    const options = this.attributeDefsArray.at(1)?.get('options')?.value;
    return options && options.length > 0 ? options.length : 1;
  }

  get attribute1Name(): string {
    return (
      this.attributeDefsArray.controls[0]?.get('name')?.value || 'Variation 1'
    );
  }

  get attribute2Name(): string {
    return (
      this.attributeDefsArray.controls[1]?.get('name')?.value || 'Variation 2'
    );
  }

  protected getAttributeSuggestions(index: number): AttributeInfoDTO[] {
    return this.attributeSuggestions[index] || [];
  }

  protected getOptionSuggestions(
    attrIndex: number,
    optIndex: number,
  ): AttributeOptionInfoDTO[] {
    return this.optionSuggestions[`${attrIndex}-${optIndex}`] || [];
  }
}
