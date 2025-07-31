import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import Paginator from "../../components/Paginator/Paginator";
import type { PaginationData } from "../../shared/types";

describe("Paginator", () => {
  const mockOnPageChange = vi.fn();

  const initialPagination: PaginationData = {
    currentPage: 0,
    size: 10,
    totalPages: 5,
    first: true,
    last: false,
  };

  afterEach(() => {
    cleanup();
    mockOnPageChange.mockReset();
  });

  it("should render the Paginator component and display 1 as the page number", () => {
    render(
      <Paginator
        pagination={initialPagination}
        onPageChange={mockOnPageChange}
      />
    );

    // expects
    expect(screen.getByTestId("paginator")).toBeInTheDocument();
    expect(screen.getByTestId("page-number")).toHaveTextContent("1");
  });

  it("should render the Paginator component and, when the 'next page' button is clicked, change the page number", async () => {
    const { rerender } = render(
      <Paginator
        pagination={initialPagination}
        onPageChange={mockOnPageChange}
      />
    );

    // first render expects
    expect(screen.getByTestId("paginator")).toBeInTheDocument();
    expect(screen.getByTestId("page-number")).toHaveTextContent("1");

    // click next page button event
    const goToNextBtn = screen.getByTestId("go-to-next");
    await userEvent.click(goToNextBtn);

    const nextPage = initialPagination.currentPage + 1;
    const updatedPagination = { ...initialPagination, currentPage: nextPage };

    // rerender
    rerender(
      <Paginator
        pagination={updatedPagination}
        onPageChange={mockOnPageChange}
      />
    );

    // expects after clicking button and rerender
    expect(mockOnPageChange).toHaveBeenCalledExactlyOnceWith(
      updatedPagination.currentPage
    );
    expect(screen.getByTestId("page-number")).toHaveTextContent("2");
  });

  it("should render the Paginator component and, when the 'go to last page' button is clicked, change the page number to the last", async () => {
    const { rerender } = render(
      <Paginator
        pagination={initialPagination}
        onPageChange={mockOnPageChange}
      />
    );

    // first render expects
    expect(screen.getByTestId("paginator")).toBeInTheDocument();
    expect(screen.getByTestId("page-number")).toHaveTextContent("1");

    // click next page button event
    const goToNextBtn = screen.getByTestId("go-to-last");
    await userEvent.click(goToNextBtn);

    const lastPage = initialPagination.totalPages - 1;
    const updatedPagination = { ...initialPagination, currentPage: lastPage };

    // rerender
    rerender(
      <Paginator
        pagination={updatedPagination}
        onPageChange={mockOnPageChange}
      />
    );

    // expects after clicking button and rerender
    expect(mockOnPageChange).toHaveBeenCalledExactlyOnceWith(
      updatedPagination.currentPage
    );
    expect(screen.getByTestId("page-number")).toHaveTextContent(
      initialPagination.totalPages.toString()
    );
  });
});
