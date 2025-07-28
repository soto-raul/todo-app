import axios from "axios";
import type { FilterCriteria, ToDo, ToDoCreationData } from "../shared/types";

const API_BASE_URL = "http://localhost:9090/todos";

const instance = axios.create({ baseURL: API_BASE_URL, timeout: 2000 });

// GET("/todos")
export const getAllToDos = async (
  filters: FilterCriteria,
  page: number,
  size: number,
  sortBy: string,
  order: string
) => {
  try {
    const response = await instance.get(API_BASE_URL, {
      params: {
        ...filters,
        page: page,
        size: size,
        sortBy: sortBy,
        order: order,
      },
    });
    return response;
  } catch (error) {
    console.error(error);
  }
};

// POST("/todos")
export const addToDo = async (newToDo: ToDoCreationData) => {
  try {
    const response = await instance.post(API_BASE_URL, newToDo);
    return response;
  } catch (error) {
    console.error(error);
  }
};

// PUT("/todos/{id}")
export const updateToDo = async (toDoId: number, updatedToDo: ToDo) => {
  try {
    const response = await instance.put(
      API_BASE_URL + "/" + toDoId,
      updatedToDo
    );
    return response;
  } catch (error) {
    console.error(error);
  }
};

// PUT("/todos/{id}/done")
export const markAsDone = async (toDoId: number) => {
  try {
    const response = await instance.put(API_BASE_URL + "/" + toDoId + "/done");
    return response;
  } catch (error) {
    console.error(error);
  }
};

// PUT("/todos/{id}/undone")
export const markAsUndone = async (toDoId: number) => {
  try {
    const response = await instance.put(
      API_BASE_URL + "/" + toDoId + "/undone"
    );
    return response;
  } catch (error) {
    console.error(error);
  }
};

//DELETE("/todos/{id}")
export const deleteToDo = async (toDoId: number) => {
  try {
    const response = await instance.delete(API_BASE_URL + "/" + toDoId);
    return response;
  } catch (error) {
    console.error(error);
  }
};
