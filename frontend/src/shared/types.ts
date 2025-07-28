export interface ToDo {
  id: number;
  name: string;
  dueDate: Date | null;
  isDone: Status;
  doneDate: Date | null;
  priority: Priority;
  creationDate: Date;
}

export interface ToDoCreationData {
  name: string;
  dueDate: Date | null;
  priority: Priority;
}

export interface FilterCriteria {
  name: string | null;
  priority: Priority | null;
  doneStatus: Status | null;
}

export type Status = "DONE" | "NOT_DONE";

export type Priority = "HIGH" | "MEDIUM" | "LOW";

export interface PaginationData {
  currentPage: number;
  size: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export type SortingOrder = "ASC" | "DESC";
