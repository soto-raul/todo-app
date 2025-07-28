import "./MetricsDashboard.css";

const MetricsDashboard = ({
  metricsData,
}: {
  metricsData: Map<string, number>;
}) => {
  const convertSecondsToHHMMSS = (timeInSeconds: number) => {
    const hours = Math.floor(timeInSeconds / 3600);
    const minutes = Math.floor((timeInSeconds % 3600) / 60);
    const seconds = Math.round(timeInSeconds % 60);

    const pad = (n: number) => n.toString().padStart(2, "0");

    return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
  };

  return (
    <section>
      <div className="metrics-container">
        <div>
          <h3>Average time to complete all tasks:</h3>
          <p>
            {metricsData.has("ALL")
              ? convertSecondsToHHMMSS(metricsData.get("ALL")!!)
              : "--:--:--"}
          </p>
        </div>
        <div>
          <h3>Average time to complete all tasks by priority:</h3>
          <p>
            <b>HIGH: </b>
            {metricsData.has("HIGH")
              ? convertSecondsToHHMMSS(metricsData.get("HIGH")!!)
              : "--:--:--"}
          </p>
          <p>
            <b>MEDIUM: </b>
            {metricsData.has("MEDIUM")
              ? convertSecondsToHHMMSS(metricsData.get("MEDIUM")!!)
              : "--:--:--"}
          </p>
          <p>
            <b>LOW: </b>
            {metricsData.has("LOW")
              ? convertSecondsToHHMMSS(metricsData.get("LOW")!!)
              : "--:--:--"}
          </p>
        </div>
      </div>
    </section>
  );
};

export default MetricsDashboard;
