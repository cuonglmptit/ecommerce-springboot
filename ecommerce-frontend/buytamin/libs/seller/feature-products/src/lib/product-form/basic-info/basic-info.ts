import { Component, inject, output, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ProductFormData } from '../product-form-data';
import { CategoryTreeNode } from '@buytamin/seller/data-access';
import { CategorySelector } from '../category-selector/category-selector';

@Component({
  selector: 'seller-basic-info',
  imports: [ReactiveFormsModule, CategorySelector],
  templateUrl: './basic-info.html',
  styleUrl: './basic-info.scss',
})
export class BasicInfo {
  protected readonly basicInfoGroup =
    inject(ProductFormData).form.controls.basicInfo;

  protected isCategoryModalVisible = signal(false);
  protected selectedCategoryPathDisplay = signal('');

  categoryChanged = output<number>();

  openCategoryModal(): void {
    this.isCategoryModalVisible.set(true);
  }

  onCategoryConfirmed(path: CategoryTreeNode[]): void {
    if (path.length === 0) return;

    // 1. Tạo chuỗi hiển thị
    this.selectedCategoryPathDisplay.set(path.map((c) => c.name).join(' > '));

    // 2. Lấy ID danh mục lá gán vào Reactive Form
    const leafCategory = path[path.length - 1];
    this.basicInfoGroup.controls.categoryId.setValue(leafCategory.id);

    // 3. Bắn event báo cho ProductForm
    this.categoryChanged.emit(leafCategory.id);
  }
}
