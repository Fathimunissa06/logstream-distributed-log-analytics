import { useState } from "react";
import {
  AlertCircle,
  BellRing,
  CheckCircle2,
  Clock3,
  Plus,
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
  const [showForm, setShowForm] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    service: "billing-api",
    condition: "Error rate",
    threshold: "",
    severity: "HIGH",
    enabled: true,
  });

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setShowForm(false);
  };

  const handleCancel = () => {
    setShowForm(false);
  };

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

        <div className="alerts-heading-actions">
          <div className="live-status">
            <BellRing size={15} />
            Alert monitoring
          </div>

          <button
            type="button"
            className="alert-create-button"
            onClick={() => setShowForm((current) => !current)}
          >
            <Plus size={16} />
            Create Alert
          </button>
        </div>
      </div>

      {showForm && (
        <section className="alert-config-panel">
          <div className="alert-config-header">
            <div>
              <h2>Create Alert</h2>
              <p>Define a rule for monitoring service activity.</p>
            </div>
          </div>

          <form className="alert-config-form" onSubmit={handleSubmit}>
            <div className="alert-form-grid">
              <label className="alert-form-field">
                <span>Alert Name</span>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="e.g. High Error Rate"
                  required
                />
              </label>

              <label className="alert-form-field">
                <span>Service</span>
                <select
                  name="service"
                  value={formData.service}
                  onChange={handleChange}
                >
                  <option value="billing-api">billing-api</option>
                  <option value="payment-service">payment-service</option>
                  <option value="order-service">order-service</option>
                  <option value="auth-service">auth-service</option>
                </select>
              </label>

              <label className="alert-form-field">
                <span>Condition</span>
                <select
                  name="condition"
                  value={formData.condition}
                  onChange={handleChange}
                >
                  <option value="Error rate">Error rate</option>
                  <option value="Response time">Response time</option>
                  <option value="Log count">Log count</option>
                  <option value="Database connection">
                    Database connection
                  </option>
                </select>
              </label>

              <label className="alert-form-field">
                <span>Threshold</span>
                <input
                  type="text"
                  name="threshold"
                  value={formData.threshold}
                  onChange={handleChange}
                  placeholder="e.g. 5%"
                  required
                />
              </label>

              <label className="alert-form-field">
                <span>Severity</span>
                <select
                  name="severity"
                  value={formData.severity}
                  onChange={handleChange}
                >
                  <option value="CRITICAL">Critical</option>
                  <option value="HIGH">High</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="LOW">Low</option>
                </select>
              </label>

              <label className="alert-toggle-field">
                <input
                  type="checkbox"
                  name="enabled"
                  checked={formData.enabled}
                  onChange={handleChange}
                />
                <span>
                  <strong>Enable alert</strong>
                  <small>Start monitoring immediately</small>
                </span>
              </label>
            </div>

            <div className="alert-form-actions">
              <button
                type="button"
                className="alert-cancel-button"
                onClick={handleCancel}
              >
                Cancel
              </button>

              <button type="submit" className="alert-save-button">
                Save Alert
              </button>
            </div>
          </form>
        </section>
      )}

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
