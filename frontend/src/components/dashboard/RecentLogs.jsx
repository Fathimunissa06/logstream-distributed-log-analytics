import { AlertCircle, Info, TriangleAlert } from "lucide-react";

function RecentLogs({ logs }) {
  const getLevelIcon = (level) => {
    if (level === "ERROR") {
      return <AlertCircle size={15} />;
    }

    if (level === "WARN") {
      return <TriangleAlert size={15} />;
    }

    return <Info size={15} />;
  };

  return (
    <section className="logs-section">
      <div className="section-header">
        <div>
          <h2>Recent Logs</h2>
          <p>Latest events received across monitored services.</p>
        </div>

        <button className="view-all-button">
          View all
        </button>
      </div>

      <div className="logs-table-wrapper">
        <table className="logs-table">
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Level</th>
              <th>Service</th>
              <th>Message</th>
              <th>Host</th>
            </tr>
          </thead>

          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td className="log-timestamp">
                  {log.timestamp}
                </td>

                <td>
                  <span
                    className={`log-level ${log.level.toLowerCase()}`}
                  >
                    {getLevelIcon(log.level)}
                    {log.level}
                  </span>
                </td>

                <td className="log-service">
                  {log.service}
                </td>

                <td className="log-message">
                  {log.message}
                </td>

                <td className="log-host">
                  {log.host}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default RecentLogs;