import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import ToDoModalForm from "../../components/ToDoModalForm/ToDoModalForm";
import type { ToDo } from "../../shared/types";

describe("ToDoModalForm", () => {
  const mockOnCancel = vi.fn();
  const mockOnSave = vi.fn();

  afterEach(() => {
    cleanup();
    vi.resetAllMocks();
  });

  it("should render the ToDoModalForm with all its inputs in their default state -> the form is called to add a new ToDo", () => {
    render(
      <ToDoModalForm
        initialData={null}
        onCancel={mockOnCancel}
        onSave={mockOnSave}
      />
    );

    //expects
    expect(screen.getByTestId("modal-form-container")).toBeInTheDocument();
    expect(screen.getByTestId("todo-form")).toHaveFormValues({
      name: "",
      priority: "HIGH",
      dueDate: "",
    });
    expect(screen.getByTestId("name-form-input")).toBeInTheDocument();
    expect(screen.getByTestId("form-error-msg")).toBeInTheDocument();
    expect(screen.getByTestId("priority-form-input")).toBeInTheDocument();
    expect(screen.getByTestId("due-date-form-input")).toBeInTheDocument();
  });

  it("should render the ToDoModalForm with all its inputs filled with a To Do's data -> the form is called to edit an existing ToDo", async () => {
    const inputToDo: ToDo = {
      id: 1,
      name: "Todo 1",
      dueDate: null,
      isDone: "NOT_DONE",
      doneDate: null,
      priority: "HIGH",
      creationDate: new Date(),
    };

    render(
      <ToDoModalForm
        initialData={inputToDo}
        onCancel={mockOnCancel}
        onSave={mockOnSave}
      />
    );

    //expects
    expect(screen.getByTestId("modal-form-container")).toBeInTheDocument();
    expect(screen.getByTestId("name-form-input")).toBeInTheDocument();
    expect(screen.queryByTestId("form-error-msg")).not.toBeInTheDocument();

    expect(screen.getByTestId("priority-form-input")).toBeInTheDocument();
    expect(screen.getByTestId("due-date-form-input")).toBeInTheDocument();
    expect(screen.getByTestId("todo-form")).toHaveFormValues({
      name: inputToDo.name,
      priority: inputToDo.priority,
      dueDate: "",
    });
  });

  it("should call the onCancel method once when clicking the 'Cancel' button", async () => {
    render(
      <ToDoModalForm
        initialData={null}
        onCancel={mockOnCancel}
        onSave={mockOnSave}
      />
    );

    const cancelBtn = screen.getByTestId("cancel-btn");

    //expects on render
    expect(screen.getByTestId("modal-form-container")).toBeInTheDocument();
    expect(screen.getByTestId("todo-form")).toBeInTheDocument();
    expect(cancelBtn).toBeInTheDocument();

    // click cancel button event
    await userEvent.click(cancelBtn);

    expect(mockOnCancel).toHaveBeenCalledOnce();
  });

  it("should call the onCancel method once when clicking the 'Save' button and the input are valid", async () => {
    const inputToDo: ToDo = {
      id: 1,
      name: "Todo 1",
      dueDate: new Date("2025-08-20"),
      isDone: "NOT_DONE",
      doneDate: null,
      priority: "HIGH",
      creationDate: new Date(),
    };

    render(
      <ToDoModalForm
        initialData={inputToDo}
        onCancel={mockOnCancel}
        onSave={mockOnSave}
      />
    );

    const saveBtn = screen.getByTestId("submit-form-btn");

    //expects on render
    expect(screen.getByTestId("modal-form-container")).toBeInTheDocument();
    expect(screen.getByTestId("todo-form")).toBeInTheDocument();
    expect(saveBtn).toBeInTheDocument();

    // click save button event
    await userEvent.click(saveBtn);

    expect(mockOnSave).toHaveBeenCalledOnce();
  });

  it("should not call the onCancel method when clicking the 'Save' button if the inputs are not valid", async () => {
    render(
      <ToDoModalForm
        initialData={null}
        onCancel={mockOnCancel}
        onSave={mockOnSave}
      />
    );

    const saveBtn = screen.getByTestId("submit-form-btn");

    //expects on render
    expect(screen.getByTestId("modal-form-container")).toBeInTheDocument();
    expect(screen.getByTestId("todo-form")).toBeInTheDocument();
    expect(saveBtn).toBeInTheDocument();

    // click save button event
    await userEvent.click(saveBtn);

    expect(mockOnSave).not.toHaveBeenCalled();
  });
});
