export type SortDirection = "asc" | "desc" | null;

export interface TableHeader {
  key?: string;
  name?: string;
  i18nKey?: string;
  position: number;
  sortable?: boolean;
  show?: boolean;
}

export interface TableSortEvent {
  key: string;
  direction: SortDirection;
}

export interface TablePageEvent {
  page: number;
}

export interface TablePageSizeEvent {
  pageSize: number;
}
