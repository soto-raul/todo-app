import "./MetricsDashboard.css";

const MetricsDashboard = ({
  metricsData,
}: {
  metricsData: Map<string, number>;
}) => {
  // default value to display
  const defaultDisplayValue = "--:--:--";

  const convertSecondsToHHMMSS = (timeInSeconds: number) => {
    if (timeInSeconds === 0) {
      return defaultDisplayValue;
    }
    const hours = Math.floor(timeInSeconds / 3600);
    const minutes = Math.floor((timeInSeconds % 3600) / 60);
    const seconds = Math.round(timeInSeconds % 60);

    const pad = (n: number) => n.toString().padStart(2, "0");

    return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
  };

  return (
    <section data-testid="metrics-component">
      <div className="metrics-container">
        <div>
          <h3>Average time to complete all tasks:</h3>
          <p data-testid="all-todos-avg">
            {metricsData.has("ALL")
              ? convertSecondsToHHMMSS(metricsData.get("ALL")!)
              : defaultDisplayValue}
          </p>
        </div>
        <div>
          <h3>Average time to complete all tasks by priority:</h3>
          <p data-testid="high-avg">
            <b>HIGH: </b>
            {metricsData.has("HIGH")
              ? convertSecondsToHHMMSS(metricsData.get("HIGH")!)
              : defaultDisplayValue}
          </p>
          <p data-testid="medium-avg">
            <b>MEDIUM: </b>
            {metricsData.has("MEDIUM")
              ? convertSecondsToHHMMSS(metricsData.get("MEDIUM")!)
              : defaultDisplayValue}
          </p>
          <p data-testid="low-avg">
            <b>LOW: </b>
            {metricsData.has("LOW")
              ? convertSecondsToHHMMSS(metricsData.get("LOW")!)
              : defaultDisplayValue}
          </p>
        </div>
      </div>
    </section>
  );
};

export default MetricsDashboard;
