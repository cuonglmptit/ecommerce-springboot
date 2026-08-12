export interface AttributeInfo {
  id: string;
  name: string;
  code: string;
  scope: 'GLOBAL' | 'SHOP';
  shopId?: number | null;
  status: string;
}

export interface AttributeOptionInfo {
  id: string;
  value: string;
  attributeId: string;
  attributeName: string;
  shopId?: number | null;
  scope: 'GLOBAL' | 'SHOP';
  status: string;
}
