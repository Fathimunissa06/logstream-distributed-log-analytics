import {
  AlertCircle,
  Info,
  TriangleAlert,
} from "lucide-react";

function LogTable({ logs }) {
  const getLevelIcon = (level) => {
    if (level === "ERROR") {
      return <AlertCircle size={14} />;
    }

    if (level === "WARN") {
      return <TriangleAlert size={14} />;
    }

    return <Info size={14} />;
  };

  if (logs.length === 0) {
    return (
      <div className="empty-logs">
        <Info size={22} />
        <h3>No logs found</h3>
        <p>
          Try changing your search query or filters.
        </p>
      </div>
    );
  }

  return (
    <div className="search-results-table">
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
          {logs.map((log, index) => (
            <tr
  key={`${log.timestamp}-${log.service}-${log.level}-${log.message}-${index}`}
>
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
  );
}

export default LogTable;