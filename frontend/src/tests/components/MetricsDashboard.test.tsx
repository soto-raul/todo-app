import { cleanup, render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it } from "vitest";
import MetricsDashboard from "../../components/MetricsDashboard/MetricsDashboard";

describe("MetricsDashboard", () => {
  afterEach(() => {
    cleanup();
  });

  // function to render component. returns necessary DOM elements for validation
  const renderMetricsDashboard = (metrics: Map<string, number>) => {
    render(<MetricsDashboard metricsData={metrics} />);

    return {
      allToDosAvg: screen.getByTestId("all-todos-avg"),
      highAvg: screen.getByTestId("high-avg"),
      mediumAvg: screen.getByTestId("medium-avg"),
      lowAvg: screen.getByTestId("low-avg"),
    };
  };

  it("should render MetricsDashboard with default data when no To Dos have been marked as DONE", () => {
    // metrics data when there are no To Dos marked as DONE
    const metrics: Map<string, number> = new Map<string, number>();

    const { allToDosAvg, highAvg, mediumAvg, lowAvg } =
      renderMetricsDashboard(metrics);

    // expects
    expect(allToDosAvg).toBeInTheDocument();
    expect(allToDosAvg).toHaveTextContent(/--:--:--/i);
    expect(highAvg).toBeInTheDocument();
    expect(highAvg).toHaveTextContent(/--:--:--/i);
    expect(mediumAvg).toBeInTheDocument();
    expect(mediumAvg).toHaveTextContent(/--:--:--/i);
    expect(lowAvg).toBeInTheDocument();
    expect(lowAvg).toHaveTextContent(/--:--:--/i);
  });

  it("should render MetricsDashboard with correct metrics data when there are only To Dos of HIGH priority marked as DONE", () => {
    // metrics data only HIGH priority To Dos are marked as DONE
    const metrics: Map<string, number> = new Map<string, number>([
      ["ALL", 36000],
      ["HIGH", 36000],
    ]);

    const { allToDosAvg, highAvg, mediumAvg, lowAvg } =
      renderMetricsDashboard(metrics);

    // expects
    expect(allToDosAvg).toBeInTheDocument();
    expect(allToDosAvg).not.toHaveTextContent(/--:--:--/i);
    expect(highAvg).toBeInTheDocument();
    expect(highAvg).not.toHaveTextContent(/--:--:--/i);
    expect(mediumAvg).toBeInTheDocument();
    expect(mediumAvg).toHaveTextContent(/--:--:--/i);
    expect(lowAvg).toBeInTheDocument();
    expect(lowAvg).toHaveTextContent(/--:--:--/i);
  });

  it("should render MetricsDashboard with correct metrics data when there are only To Dos of MEDIUM priority marked as DONE", () => {
    // metrics data only MEDIUM priority To Dos are marked as DONE
    const metrics: Map<string, number> = new Map<string, number>([
      ["ALL", 36000],
      ["MEDIUM", 36000],
    ]);

    const { allToDosAvg, highAvg, mediumAvg, lowAvg } =
      renderMetricsDashboard(metrics);

    // expects
    expect(allToDosAvg).toBeInTheDocument();
    expect(allToDosAvg).not.toHaveTextContent(/--:--:--/i);
    expect(highAvg).toBeInTheDocument();
    expect(highAvg).toHaveTextContent(/--:--:--/i);
    expect(mediumAvg).toBeInTheDocument();
    expect(mediumAvg).not.toHaveTextContent(/--:--:--/i);
    expect(lowAvg).toBeInTheDocument();
    expect(lowAvg).toHaveTextContent(/--:--:--/i);
  });

  it("should render MetricsDashboard with correct metrics data when there are only To Dos of LOW priority marked as DONE", () => {
    // metrics data only LOW priority To Dos are marked as DONE
    const metrics: Map<string, number> = new Map<string, number>([
      ["ALL", 36000],
      ["LOW", 36000],
    ]);

    const { allToDosAvg, highAvg, mediumAvg, lowAvg } =
      renderMetricsDashboard(metrics);

    // expects
    expect(allToDosAvg).toBeInTheDocument();
    expect(allToDosAvg).not.toHaveTextContent(/--:--:--/i);
    expect(highAvg).toBeInTheDocument();
    expect(highAvg).toHaveTextContent(/--:--:--/i);
    expect(mediumAvg).toBeInTheDocument();
    expect(mediumAvg).toHaveTextContent(/--:--:--/i);
    expect(lowAvg).toBeInTheDocument();
    expect(lowAvg).not.toHaveTextContent(/--:--:--/i);
  });

  it("should render MetricsDashboard with correct metrics data when there are  To Dos of all priorities marked as DONE", () => {
    // metrics data To Dos of all priorities are marked as DONE
    const metrics: Map<string, number> = new Map<string, number>([
      ["ALL", 36000],
      ["HIGH", 36000],
      ["MEDIUM", 36000],
      ["LOW", 36000],
    ]);

    const { allToDosAvg, highAvg, mediumAvg, lowAvg } =
      renderMetricsDashboard(metrics);

    // expects
    expect(allToDosAvg).toBeInTheDocument();
    expect(allToDosAvg).not.toHaveTextContent(/--:--:--/i);
    expect(highAvg).toBeInTheDocument();
    expect(highAvg).not.toHaveTextContent(/--:--:--/i);
    expect(mediumAvg).toBeInTheDocument();
    expect(mediumAvg).not.toHaveTextContent(/--:--:--/i);
    expect(lowAvg).toBeInTheDocument();
    expect(lowAvg).not.toHaveTextContent(/--:--:--/i);
  });
});
