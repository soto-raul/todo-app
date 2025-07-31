import { cleanup, render, screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import FilterControls from "../../components/FilterControls/FilterControls";
import type { FilterCriteria } from "../../shared/types";

describe("FilterControls", () => {
  const mockSetFilters = vi.fn(); // mockSetFilters() function mock

  afterEach(() => {
    mockSetFilters.mockReset();
    cleanup();
  });

  it("should render all filter input controls in their default state, along with the 'Search' button", () => {
    render(<FilterControls setFilters={mockSetFilters} />);

    // expect
    expect(screen.getByTestId("name-filter-input")).toBeInTheDocument();
    expect(screen.getByTestId("priority-filter-input")).toBeInTheDocument();
    expect(screen.getByTestId("status-filter-input")).toBeInTheDocument();
    expect(screen.getByTestId("search-button")).toBeInTheDocument();
  });

  it("should call setFilters() with no filter criteria applied, i.e., the inputs are in their default state.", async () => {
    render(<FilterControls setFilters={mockSetFilters} />);

    const defaultFilters = {
      name: null,
      priority: null,
      doneStatus: null,
    };

    // click search button event
    const searchButton = screen.getByTestId("search-button");
    await userEvent.click(searchButton);

    // expect
    expect(mockSetFilters).toHaveBeenCalledExactlyOnceWith(defaultFilters);
  });

  it("should call setFilters() with only name filter", async () => {
    render(<FilterControls setFilters={mockSetFilters} />);

    const expectedFilters: FilterCriteria = {
      name: "testing",
      priority: null,
      doneStatus: null,
    };

    // user type's name filter value
    const nameFilterInput = screen.getByTestId("name-filter-input");
    await userEvent.type(nameFilterInput, expectedFilters.name!!);

    // click search button event
    const searchButton = screen.getByTestId("search-button");
    await userEvent.click(searchButton);

    // expects
    expect(mockSetFilters).toHaveBeenCalledExactlyOnceWith(expectedFilters);
  });

  it("should call setFilters() with only priority filter", async () => {
    render(<FilterControls setFilters={mockSetFilters} />);

    const expectedFilters: FilterCriteria = {
      name: null,
      priority: "HIGH",
      doneStatus: null,
    };

    // user selects priority filter value
    const priorityFilterInput = screen.getByTestId("priority-filter-input");
    userEvent.selectOptions(
      priorityFilterInput,
      expectedFilters.priority as string
    );

    // click search button event
    const searchButton = screen.getByTestId("search-button");
    await userEvent.click(searchButton);

    // expects
    expect(mockSetFilters).toHaveBeenCalledExactlyOnceWith(expectedFilters);
  });

  it("should call setFilters() with only done status filter", async () => {
    render(<FilterControls setFilters={mockSetFilters} />);

    const expectedFilters: FilterCriteria = {
      name: null,
      priority: null,
      doneStatus: "DONE",
    };

    // user selects done status filter value
    const statusFilterInput = screen.getByTestId("status-filter-input");
    userEvent.selectOptions(
      statusFilterInput,
      expectedFilters.doneStatus as string
    );

    // click search button event
    const searchButton = screen.getByTestId("search-button");
    await userEvent.click(searchButton);

    // expects
    expect(mockSetFilters).toHaveBeenCalledExactlyOnceWith(expectedFilters);
  });

  it("should call setFilters() with all filters", async () => {
    render(<FilterControls setFilters={mockSetFilters} />);

    const expectedFilters: FilterCriteria = {
      name: "testing",
      priority: "MEDIUM",
      doneStatus: "DONE",
    };

    // user defines all filters values
    const nameFilterInput = screen.getByTestId("name-filter-input");
    await userEvent.type(nameFilterInput, expectedFilters.name!!);

    const priorityFilterInput = screen.getByTestId("priority-filter-input");
    userEvent.selectOptions(
      priorityFilterInput,
      expectedFilters.priority as string
    );

    const statusFilterInput = screen.getByTestId("status-filter-input");
    userEvent.selectOptions(
      statusFilterInput,
      expectedFilters.doneStatus as string
    );

    // click search button event
    const searchButton = screen.getByTestId("search-button");
    await userEvent.click(searchButton);

    // expects
    expect(mockSetFilters).toHaveBeenCalledExactlyOnceWith(expectedFilters);
  });
});
