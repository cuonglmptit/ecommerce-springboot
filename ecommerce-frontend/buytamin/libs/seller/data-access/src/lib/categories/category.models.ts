import { AttributeInfo } from '../attributes/attribute.models';

export interface CategoryInfo {
  id: number;
  name: string;
  description?: string | null;
  path: string;
  depth: number;
  sortOrder: number;
  hasChildren: boolean;
}


export interface CategoryTreeNode {
  id: number;
  name: string;
  description?: string | null;
  path: string;
  depth: number;
  sortOrder: number;
  hasChildren: boolean;
  parentId?: number | null;
  children: CategoryTreeNode[];
}


export interface CategoryAttributeInfo {
  id: number;
  sortOrder: number;
  filterable: boolean;
  filterType: 'CHECKBOX' | 'RADIO' | 'RANGE' | 'TEXT' | string;
  attribute: AttributeInfo;
}

/*
export interface CategoryAttributeInfo {
  id: number;
  sortOrder: number;
  filterable: boolean;
  filterType: 'CHECKBOX' | 'RADIO' | 'RANGE' | 'TEXT' | string;
  attribute: AttributeInfo;
}
*/
