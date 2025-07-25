import type { ToDo } from "./types";

// Validate that an object's type equals interface ToDo
export function isToDo(obj: any): obj is ToDo {
  return "id" in obj;
}

// check if To Do name is valid
export function isNameValid(name: string): boolean {
  return name !== null && name !== "" && name.trim().length > 0;
}
