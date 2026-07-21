export interface BaseProduct {
  id: string;
  name: string;
  categoryId: number;
  description: string;
  imageUrl: string;
  publicPrice: number; // Giá bán cho khách hàng
}
