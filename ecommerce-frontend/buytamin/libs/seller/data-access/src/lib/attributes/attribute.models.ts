export interface AttributeInfoDTO {
  id: string;
  name: string;
  code: string;
  scope: 'GLOBAL' | 'SHOP';
  shopId?: number | null;
  status: string;
}

export interface AttributeOptionInfoDTO {
  id: string;
  value: string;
  attributeId: string;
  attributeName: string;
  shopId?: number | null;
  scope: 'GLOBAL' | 'SHOP';
  status: string;
}
