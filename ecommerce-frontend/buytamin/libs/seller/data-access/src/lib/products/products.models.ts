import { BaseProduct } from '@buytamin/shared/models';

export interface ProductsEntity {
  id: string | number;
  name: string;
}

export interface ProductMedia {
  mediaId: string;
  url: string;
  alt?: string | null;
  isThumbnail: boolean;
  sortOrder: number;
}

export interface VariantAttributeInput {
  attributeId?: string | null;
  attributeName: string;
  optionId?: string | null;
  optionValue: string;
}

export interface Variant {
  sku: string;
  price: number;
  salePrice: number;
  stockQuantity: number;
  attributes: VariantAttributeInput[];
  media?: ProductMedia[];
}

export interface ProductSpecificationInput {
  attributeId: string;
  attributeName: string;
  attributeCode?: string | null;
  optionId?: string | null;
  optionValue?: string | null;
  rawValue?: string | null;
}

// Chỉ kế thừa name, description, categoryId từ BaseProduct, loại bỏ id, imageUrl, publicPrice
// Hoặc dùng Pick để chỉ chọn đúng các trường cần thiết (extends Pick<BaseProduct, 'name' | 'description'>)
export interface CreateProductRequest
  extends Omit<BaseProduct, 'id' | 'imageUrl' | 'publicPrice' | 'categoryId'> {
  shopId: number;
  categoryId: null | number;
  specifications?: ProductSpecificationInput[];
  media: ProductMedia[];
  variants: Variant[];
}
