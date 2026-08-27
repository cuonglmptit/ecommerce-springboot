import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProductFormData } from '../product-form-data';
import {
  CategoryApi,
  CategoryAttributeInfo,
  ProductSpecificationInput,
} from '@buytamin/seller/data-access';

@Component({
  selector: 'seller-product-specification',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './product-specification.html',
  styleUrl: './product-specification.scss',
})
export class ProductSpecification implements OnInit {
  protected readonly formDataService = inject(ProductFormData);
  private readonly categoryApi = inject(CategoryApi);

  protected readonly specAttributes = signal<CategoryAttributeInfo[]>([]);
  protected readonly isLoading = signal<boolean>(false);

  // Model lưu trữ giá trị nhập của các specs
  protected readonly specValues = signal<Record<string, string>>({});

  ngOnInit(): void {
    const initialCatId =
      this.formDataService.form.controls.basicInfo.controls.categoryId.value;
    if (initialCatId) {
      this.loadSpecifications(initialCatId);
    }

    this.formDataService.form.controls.basicInfo.controls.categoryId.valueChanges.subscribe(
      (catId) => {
        if (catId) {
          this.loadSpecifications(catId);
        } else {
          this.specAttributes.set([]);
          this.specValues.set({});
          this.formDataService.specificationsList.set([]);
        }
      },
    );
  }

  private loadSpecifications(categoryId: number): void {
    this.isLoading.set(true);
    this.categoryApi
      .getCategoryAttributes(categoryId, 'SPECIFICATION')
      .subscribe({
        next: (res) => {
          const attributes = res.data || [];
          this.specAttributes.set(attributes);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      });
  }

  onSpecChange(item: CategoryAttributeInfo, value: string): void {
    this.specValues.update((prev) => ({ ...prev, [item.attribute.id]: value }));

    // Cập nhật danh sách specifications vào ProductFormData
    const specs: ProductSpecificationInput[] = Object.entries(this.specValues())
      .filter(([_, val]) => val && val.trim() !== '')
      .map(([attrId, val]) => {
        const targetAttr = this.specAttributes().find(
          (a) => a.attribute.id === attrId,
        );
        return {
          attributeId: attrId,
          attributeName: targetAttr?.attribute.name || '',
          attributeCode: targetAttr?.attribute.code || null,
          rawValue: val.trim(),
        };
      });

    this.formDataService.specificationsList.set(specs);
  }
}
