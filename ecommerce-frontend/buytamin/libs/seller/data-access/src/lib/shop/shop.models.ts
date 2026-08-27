export type ShopStatus = 'ACTIVE' | 'PENDING' | 'REJECTED' | 'SUSPENDED';

export interface ShopInfo {
  id: number;
  name: string;
  description?: string | null;
  status: ShopStatus;
  ownerId: number;
}
