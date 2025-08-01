import { cleanup, render, screen } from "@testing-library/react";
import axios from "axios";
import { afterEach, describe, expect, it, vi } from "vitest";
import App from "../App";

const mockToDos = [
  {
    id: 1,
    name: "Test ToDo",
    dueDate: "2025-08-20T17:00:00.000Z",
    isDone: "NOT_DONE",
    doneDate: null,
    priority: "HIGH",
    creationDate: "2025-08-01T10:00:00.000Z",
  },
];

const mockMetrics = new Map<string, number>([["ALL", 36000]]);

// Mock axios
vi.mock("axios");
const mockedAxios = vi.mocked(axios, true);

const mockImplementations = {
  get: () =>
    mockedAxios.get.mockImplementationOnce((url) => {
      if (url.includes("todos")) {
        return Promise.resolve({
          data: {
            content: mockToDos,
            number: 0,
            size: 10,
            totalPages: 1,
            first: true,
            last: true,
          },
        });
      }
      if (url.includes("metrics")) {
        return Promise.resolve({
          data: mockMetrics,
        });
      }
      return Promise.reject(new Error());
    }),
};

describe("App", async () => {
  afterEach(() => {
    cleanup();
    vi.resetAllMocks();
  });

  it("should render App component with its children, all in their default state", async () => {
    mockImplementations.get();
    render(<App />);

    // expects
    expect(screen.getByText(/To Do List App/i)).toBeInTheDocument();
    expect(screen.getByTestId("filter-controls-component")).toBeInTheDocument();
    expect(screen.getByTestId("add-todo-btn")).toBeInTheDocument();
    expect(
      screen.queryByTestId("modal-form-container")
    ).not.toBeInTheDocument();
    expect(screen.getByTestId("todo-list-component")).toBeInTheDocument();
    expect(screen.getByTestId("paginator-component")).toBeInTheDocument();
    expect(screen.getByTestId("metrics-component")).toBeInTheDocument();
  });
});
