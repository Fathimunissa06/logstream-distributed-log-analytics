import {
  AlertCircle,
  BellRing,
  CheckCircle2,
  Clock3,
  TriangleAlert,
} from "lucide-react";

import StatCard from "../components/dashboard/StatCard";

const mockAlerts = [
  {
    id: 1,
    name: "High Error Rate",
    service: "billing-api",
    severity: "CRITICAL",
    status: "TRIGGERED",
    condition: "Error rate > 5%",
    lastTriggered: "2 minutes ago",
  },
  {
    id: 2,
    name: "Database Connection Failure",
    service: "payment-service",
    severity: "HIGH",
    status: "TRIGGERED",
    condition: "Database connection failed",
    lastTriggered: "8 minutes ago",
  },
  {
    id: 3,
    name: "High Response Latency",
    service: "order-service",
    severity: "MEDIUM",
    status: "ACTIVE",
    condition: "Response time > 2s",
    lastTriggered: "24 minutes ago",
  },
  {
    id: 4,
    name: "Service Recovery",
    service: "auth-service",
    severity: "LOW",
    status: "RESOLVED",
    condition: "Service became healthy",
    lastTriggered: "1 hour ago",
  },
];

function getSeverityIcon(severity) {
  if (severity === "CRITICAL") {
    return <AlertCircle size={14} />;
  }

  if (severity === "HIGH") {
    return <TriangleAlert size={14} />;
  }

  if (severity === "MEDIUM") {
    return <Clock3 size={14} />;
  }

  return <BellRing size={14} />;
}

function Alerts() {
  return (
    <div className="alerts-page">
      <div className="page-heading">
        <div>
          <span className="page-eyebrow">ALERT CENTER</span>
          <h1>Alerts</h1>
          <p>
            Monitor configured alerts and review recent alert activity.
          </p>
        </div>

        <div className="live-status">
          <BellRing size={15} />
          Alert monitoring
        </div>
      </div>

      <div className="stats-grid">
        <StatCard
          label="Total Alerts"
          value="12"
          trend="+2"
          description="configured alerts"
          trendDirection="up"
        />

        <StatCard
          label="Active Alerts"
          value="5"
          trend="+1"
          description="currently active"
          trendDirection="up"
        />

        <StatCard
          label="Triggered"
          value="3"
          trend="+2"
          description="in the last hour"
          trendDirection="up"
        />

        <StatCard
          label="Resolved"
          value="4"
          trend="+3"
          description="in the last hour"
          trendDirection="up"
        />
      </div>

      <section className="alerts-section">
        <div className="alerts-section-header">
          <div>
            <h2>Alert Activity</h2>
            <p>Recent alerts across distributed services.</p>
          </div>
        </div>

        <div className="alerts-list">
          {mockAlerts.map((alert) => (
            <div className="alert-item" key={alert.id}>
              <div className="alert-main">
                <div className="alert-title-row">
                  <h3>{alert.name}</h3>

                  <span
                    className={`alert-severity ${alert.severity.toLowerCase()}`}
                  >
                    {getSeverityIcon(alert.severity)}
                    {alert.severity}
                  </span>
                </div>

                <p className="alert-condition">{alert.condition}</p>

                <div className="alert-meta">
                  <span>{alert.service}</span>
                  <span>Last triggered {alert.lastTriggered}</span>
                </div>
              </div>

              <div className={`alert-status ${alert.status.toLowerCase()}`}>
                {alert.status === "RESOLVED" && (
                  <CheckCircle2 size={14} />
                )}
                {alert.status}
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

export default Alerts;
