import {
  Component,
  computed,
  inject,
  model,
  OnInit,
  output,
  signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CategoryApi, CategoryTreeNode } from '@buytamin/seller/data-access';

@Component({
  selector: 'seller-category-selector',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-selector.html',
  styleUrl: './category-selector.scss',
})
export class CategorySelector implements OnInit {
  private readonly categoryApi = inject(CategoryApi);

  visible = model(false);
  categoryConfirmed = output<CategoryTreeNode[]>();

  protected readonly searchControl = new FormControl('', { nonNullable: true });

  // Dữ liệu Cây gốc tải từ API
  private categoryTree = signal<CategoryTreeNode[]>([]);

  // Mảng 2 chiều quản lý hiển thị các cột
  protected columns = signal<CategoryTreeNode[][]>([]);
  protected selectedPath = signal<CategoryTreeNode[]>([]);
  protected searchResults = signal<
    { item: CategoryTreeNode; displayPath: string }[]
  >([]);

  protected isSearching = signal(false);
  protected isLoading = signal(false);

  ngOnInit(): void {
    this.loadCategoryTree();
    this.setupClientSearch();
  }

  protected currentPathDisplay = computed(() =>
    this.selectedPath()
      .map((c) => c.name)
      .join(' > '),
  );

  protected isLeafSelected = computed(() => {
    const path = this.selectedPath();
    if (path.length === 0) return false;
    return !path[path.length - 1].hasChildren;
  });

  // Tải Full Cây Danh mục 1
  private loadCategoryTree(): void {
    this.isLoading.set(true);
    this.categoryApi.getCategoryTree().subscribe({
      next: (res) => {
        if (res.data) {
          this.categoryTree.set(res.data);
          this.columns.set([res.data]); // Cột 0
        }
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  // Click chọn item ở Cột colIndex -> Lấy trực tiếp children có sẵn
  selectCategory(colIndex: number, item: CategoryTreeNode): void {
    const newPath = [...this.selectedPath().slice(0, colIndex), item];
    this.selectedPath.set(newPath);

    const newColumns = this.columns().slice(0, colIndex + 1);
    if (item.hasChildren && item.children && item.children.length > 0) {
      newColumns.push(item.children);
    }
    this.columns.set(newColumns);
  }

  // Client-side Search
  private setupClientSearch(): void {
    this.searchControl.valueChanges.subscribe((query) => {
      const trimmed = query.trim().toLowerCase();
      if (!trimmed) {
        this.isSearching.set(false);
        this.searchResults.set([]);
        return;
      }

      this.isSearching.set(true);
      const flatList = this.flattenTree(this.categoryTree());

      const matches = flatList.filter((node) =>
        node.name.toLowerCase().includes(trimmed),
      );

      this.searchResults.set(
        matches.map((node) => ({
          item: node,
          displayPath: this.buildFullPath(node, flatList),
        })),
      );
    });
  }

  selectSearchResult(result: {
    item: CategoryTreeNode;
    displayPath: string;
  }): void {
    const flatList = this.flattenTree(this.categoryTree());
    const ids = result.item.path.split('/').filter(Boolean).map(Number);

    const newPath = ids
      .map((id) => flatList.find((node) => node.id === id))
      .filter((node): node is CategoryTreeNode => !!node);

    this.selectedPath.set(newPath);
  }

  isSelected(colIndex: number, categoryId: number): boolean {
    return this.selectedPath()[colIndex]?.id === categoryId;
  }

  private flattenTree(nodes: CategoryTreeNode[]): CategoryTreeNode[] {
    return nodes.reduce<CategoryTreeNode[]>((acc, node) => {
      acc.push(node);
      if (node.children && node.children.length > 0) {
        acc.push(...this.flattenTree(node.children));
      }
      return acc;
    }, []);
  }

  private buildFullPath(
    node: CategoryTreeNode,
    flatList: CategoryTreeNode[],
  ): string {
    const ids = node.path.split('/').filter(Boolean).map(Number);
    return ids
      .map((id) => flatList.find((n) => n.id === id)?.name)
      .filter(Boolean)
      .join(' > ');
  }

  confirm(): void {
    if (this.isLeafSelected()) {
      this.categoryConfirmed.emit(this.selectedPath());
      this.close();
    }
  }

  close(): void {
    this.visible.set(false);
  }
}
