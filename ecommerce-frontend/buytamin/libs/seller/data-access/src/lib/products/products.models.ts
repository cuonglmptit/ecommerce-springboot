import { BaseProduct } from '@buytamin/shared/models';

/**
 * Interface for the 'Products' data
 */
export interface ProductsEntity {
  id: string | number; // Primary ID
  name: string;
}

export interface ProductMedia {
  mediaId: string;
  isThumbnail: boolean;
  sortOrder: number;
}

export interface Variant {
  sku: string;
  price: number;
  salePrice: number;
  stockQuantity: number;
  attributeOptionIds: string[];
  variantMedia?: ProductMedia[];
}

// Kế thừa BaseProduct để lấy lại name, description, categoryId
// Đồng thời bổ sung thêm các trường đặc thù mà chỉ Seller mới có
export interface CreateProductRequest extends Omit<BaseProduct, 'id'> {
  shopId: number;
  productMedia: ProductMedia[];
  variants: Variant[];
}
