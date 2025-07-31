import { cleanup, render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import ToDoList from "../../components/ToDoList/ToDoList";
import type { SortingOrder, ToDo } from "../../shared/types";

describe("ToDoList", () => {
  // functions passed as props to ToDoList components
  const mockOnEditClick = vi.fn();
  const mockOnDeleteClick = vi.fn();
  const mockOnDoneStatusChange = vi.fn();
  const mockOnSortingChange = vi.fn();

  // sample ToDo list
  const sampleToDos: ToDo[] = [
    {
      id: 1,
      name: "Todo 1",
      dueDate: new Date("2025-08-20"),
      isDone: "NOT_DONE",
      doneDate: null,
      priority: "HIGH",
      creationDate: new Date(),
    },
    {
      id: 2,
      name: "Todo 2",
      dueDate: null,
      isDone: "NOT_DONE",
      doneDate: null,
      priority: "MEDIUM",
      creationDate: new Date(),
    },
    {
      id: 3,
      name: "Todo 3",
      dueDate: new Date("2025-09-10"),
      isDone: "NOT_DONE",
      doneDate: null,
      priority: "LOW",
      creationDate: new Date(),
    },
    {
      id: 4,
      name: "Todo 4",
      dueDate: null,
      isDone: "NOT_DONE",
      doneDate: null,
      priority: "MEDIUM",
      creationDate: new Date(),
    },
  ];

  afterEach(() => {
    cleanup();
    vi.resetAllMocks();
  });

  it("should render an empty ToDoList with the appropriate message", () => {
    render(
      <ToDoList
        toDos={[]}
        currSorting={new Map<string, SortingOrder>()}
        onEditClick={mockOnEditClick}
        onDeleteClick={mockOnDeleteClick}
        onDoneStatusChange={mockOnDoneStatusChange}
        onSortingChange={mockOnSortingChange}
      />
    );

    const noToDosMsg = screen.getByTestId("no-todos-msg");

    // expects
    expect(noToDosMsg).toBeInTheDocument();
    expect(noToDosMsg).toHaveTextContent(/No To Do tasks were found/i);
  });

  it("should render the ToDoList displaying all the ToDo objects passed as props in the default order", () => {
    render(
      <ToDoList
        toDos={sampleToDos}
        currSorting={new Map<string, SortingOrder>()}
        onEditClick={mockOnEditClick}
        onDeleteClick={mockOnDeleteClick}
        onDoneStatusChange={mockOnDoneStatusChange}
        onSortingChange={mockOnSortingChange}
      />
    );

    // get all rows
    const tableRows = within(screen.getByTestId("table-body")).getAllByRole(
      "row"
    );

    // expects
    expect(screen.queryByTestId("no-todos-msg")).not.toBeInTheDocument();
    expect(tableRows).toHaveLength(sampleToDos.length); // all To Dos in table
    // verify that they are rendered in the default order (same as list)
    expect(tableRows[0].querySelector("td:nth-child(2)")).toHaveTextContent(
      sampleToDos[0].name
    );
    expect(tableRows[1].querySelector("td:nth-child(2)")).toHaveTextContent(
      sampleToDos[1].name
    );
    expect(tableRows[2].querySelector("td:nth-child(2)")).toHaveTextContent(
      sampleToDos[2].name
    );
    expect(tableRows[3].querySelector("td:nth-child(2)")).toHaveTextContent(
      sampleToDos[3].name
    );
  });

  it("should render the ToDoList with all the ToDo objects and, after clicking the delete button, remove the ToDo from the list", async () => {
    const { rerender } = render(
      <ToDoList
        toDos={sampleToDos}
        currSorting={new Map<string, SortingOrder>()}
        onEditClick={mockOnEditClick}
        onDeleteClick={mockOnDeleteClick}
        onDoneStatusChange={mockOnDoneStatusChange}
        onSortingChange={mockOnSortingChange}
      />
    );

    // get all rows
    let tableRows = within(screen.getByTestId("table-body")).getAllByRole(
      "row"
    );

    // Expects for initial rendering
    expect(screen.queryByTestId("no-todos-msg")).not.toBeInTheDocument();
    expect(tableRows).toHaveLength(sampleToDos.length); // all To Dos in table

    // click delete button event, delete first list element
    const firstToDoDeleteBtn = within(tableRows[0]).getByText("Delete");
    await userEvent.click(firstToDoDeleteBtn);

    // rerender list
    rerender(
      <ToDoList
        toDos={sampleToDos.slice(1)}
        currSorting={new Map<string, SortingOrder>()}
        onEditClick={mockOnEditClick}
        onDeleteClick={mockOnDeleteClick}
        onDoneStatusChange={mockOnDoneStatusChange}
        onSortingChange={mockOnSortingChange}
      />
    );

    // updated table body and rows
    tableRows = within(screen.getByTestId("table-body")).getAllByRole("row");

    // expect after clicking delete button
    expect(mockOnDeleteClick).toHaveBeenCalledExactlyOnceWith(
      sampleToDos[0].id
    );
    expect(tableRows).toHaveLength(sampleToDos.length - 1);
    // verify that the deleted element is no longer displayed
    expect(tableRows[0].querySelector("td:nth-child(2)")).not.toHaveTextContent(
      sampleToDos[0].name
    );
  });

  it("should render the ToDoList with all the ToDo objects (default order) and, after clicking the edit button, call onEditClick() with the correct ToDo as arg", async () => {
    render(
      <ToDoList
        toDos={sampleToDos}
        currSorting={new Map<string, SortingOrder>()}
        onEditClick={mockOnEditClick}
        onDeleteClick={mockOnDeleteClick}
        onDoneStatusChange={mockOnDoneStatusChange}
        onSortingChange={mockOnSortingChange}
      />
    );

    // get all rows
    const tableRows = within(screen.getByTestId("table-body")).getAllByRole(
      "row"
    );

    // Expects for initial rendering
    expect(screen.queryByTestId("no-todos-msg")).not.toBeInTheDocument();
    expect(tableRows).toHaveLength(sampleToDos.length); // all To Dos in table

    // click delete button event, edit first list element
    const firstToDoEditBtn = within(tableRows[0]).getByText("Edit");
    await userEvent.click(firstToDoEditBtn);

    // expects
    expect(mockOnEditClick).toHaveBeenCalledExactlyOnceWith(sampleToDos[0]);
  });

  it("should render the ToDoList with all the ToDo objects (default order) and, after clicking the checkbox, mark ToDo as DONE and update displayed data", async () => {
    const { rerender } = render(
      <ToDoList
        toDos={sampleToDos}
        currSorting={new Map<string, SortingOrder>()}
        onEditClick={mockOnEditClick}
        onDeleteClick={mockOnDeleteClick}
        onDoneStatusChange={mockOnDoneStatusChange}
        onSortingChange={mockOnSortingChange}
      />
    );

    // get all rows
    let tableRows = within(screen.getByTestId("table-body")).getAllByRole(
      "row"
    );
    let targetCheckbox = within(tableRows[0]).getByRole("checkbox");

    // Expects for initial rendering
    expect(screen.queryByTestId("no-todos-msg")).not.toBeInTheDocument();
    expect(tableRows).toHaveLength(sampleToDos.length); // all To Dos in table
    // target To Do status checkbox is not checked
    expect(targetCheckbox).not.toBeChecked();

    // click done status checkbox event, mark first element as DONE
    const firstToDoCheckbox = within(tableRows[0]).getByRole("checkbox");
    await userEvent.click(firstToDoCheckbox);

    const updatedToDos: ToDo[] = sampleToDos.map((toDo: ToDo) =>
      toDo.id === 1 ? { ...toDo, isDone: "DONE" } : toDo
    );

    // rerender list
    rerender(
      <ToDoList
        toDos={updatedToDos}
        currSorting={new Map<string, SortingOrder>()}
        onEditClick={mockOnEditClick}
        onDeleteClick={mockOnDeleteClick}
        onDoneStatusChange={mockOnDoneStatusChange}
        onSortingChange={mockOnSortingChange}
      />
    );

    // updated table body and rows
    tableRows = within(screen.getByTestId("table-body")).getAllByRole("row");
    targetCheckbox = within(tableRows[0]).getByRole("checkbox");

    // expect after clicking checkbox
    expect(mockOnDoneStatusChange).toHaveBeenCalledExactlyOnceWith(
      sampleToDos[0].id,
      "DONE"
    );
    expect(tableRows).toHaveLength(updatedToDos.length);
    // target To Do status checkbox is checked
    expect(targetCheckbox).toBeChecked();
  });

  // TODO: TEST SORTING
});
