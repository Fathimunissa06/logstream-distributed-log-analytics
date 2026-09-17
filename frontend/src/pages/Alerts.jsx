import { useEffect, useState } from "react";
import "./Alerts.css";
import {
  AlertTriangle,
  Bell,
  CheckCircle,
  Clock,
  Plus,
  Trash2,
  X,
} from "lucide-react";
import {
  getAlerts,
  createAlert,
  deleteAlert,
} from "../services/alertService";

const conditionConfig = {
  "Error rate": {
    unit: "%",
    placeholder: "e.g. 5",
    description: "Trigger when the error rate exceeds this percentage.",
  },
  "Response time": {
    unit: "ms",
    placeholder: "e.g. 2000",
    description: "Trigger when response time exceeds this value.",
  },
  "Log count": {
    unit: "logs",
    placeholder: "e.g. 1000",
    description: "Trigger when log count exceeds this value.",
  },
  "Database connection": {
    unit: "",
    placeholder: "e.g. 1",
    description: "Trigger when database connection failures occur.",
  },
};

const severityConfig = {
  CRITICAL: {
    description: "Immediate attention required",
  },
  HIGH: {
    description: "High-priority issue",
  },
  MEDIUM: {
    description: "Requires monitoring",
  },
  LOW: {
    description: "Informational alert",
  },
};

function Alerts() {
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    service: "ALL",
    condition: "Error rate",
    threshold: "",
    severity: "HIGH",
    enabled: true,
  });

  useEffect(() => {
    let cancelled = false;

    async function fetchAlerts() {
      try {
        const data = await getAlerts();

        if (!cancelled) {
          setAlerts(Array.isArray(data) ? data : []);
          setLoading(false);
        }
      } catch (err) {
        console.error(err);

        if (!cancelled) {
          setError("Unable to load alerts from the server.");
          setLoading(false);
        }
      }
    }

    fetchAlerts();

    return () => {
      cancelled = true;
    };
  }, []);

  function handleChange(event) {
    const { name, value, type, checked } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  }

  function handleConditionChange(event) {
    const condition = event.target.value;

    setFormData((current) => ({
      ...current,
      condition,
      threshold: "",
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!formData.name.trim()) {
      setError("Please enter an alert name.");
      return;
    }

    if (!formData.threshold) {
      setError("Please enter a threshold.");
      return;
    }

    try {
      setError("");

      const createdAlert = await createAlert({
        name: formData.name.trim(),
        service: formData.service,
        condition: formData.condition,
        threshold: Number(formData.threshold),
        severity: formData.severity,
        enabled: formData.enabled,
      });

      setAlerts((current) => [createdAlert, ...current]);

      setFormData({
        name: "",
        service: "ALL",
        condition: "Error rate",
        threshold: "",
        severity: "HIGH",
        enabled: true,
      });

      setShowForm(false);
    } catch (err) {
      console.error(err);
      setError("Unable to create alert.");
    }
  }

  async function handleDeleteAlert(id) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this alert?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");

      await deleteAlert(id);

      setAlerts((current) =>
        current.filter((alert) => alert.id !== id)
      );
    } catch (err) {
      console.error(err);
      setError("Unable to delete alert.");
    }
  }

  const activeAlerts = alerts.filter(
    (alert) => alert.enabled
  ).length;

  const triggeredAlerts = alerts.filter(
    (alert) => alert.triggered
  ).length;

  const resolvedAlerts = alerts.filter(
    (alert) => !alert.triggered && alert.lastTriggered
  ).length;

  return (
    <div className="page-container alerts-page">
      <div className="page-header">
        <div>
          <h1>Alerts</h1>
          <p>
            Configure and monitor alerts for your distributed services.
          </p>
        </div>

        <button
          className="primary-button"
          onClick={() => {
            setError("");
            setShowForm(true);
          }}
        >
          <Plus size={18} />
          Create Alert
        </button>
      </div>

      {error && (
        <div className="alert-error-message">
          <AlertTriangle size={18} />
          <span>{error}</span>

          <button
            onClick={() => setError("")}
            aria-label="Close error"
          >
            <X size={16} />
          </button>
        </div>
      )}

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">
            <Bell size={22} />
          </div>

          <div>
            <span className="stat-label">Total Alerts</span>
            <strong>{alerts.length}</strong>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <CheckCircle size={22} />
          </div>

          <div>
            <span className="stat-label">Active</span>
            <strong>{activeAlerts}</strong>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <AlertTriangle size={22} />
          </div>

          <div>
            <span className="stat-label">Triggered</span>
            <strong>{triggeredAlerts}</strong>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">
            <Clock size={22} />
          </div>

          <div>
            <span className="stat-label">Resolved</span>
            <strong>{resolvedAlerts}</strong>
          </div>
        </div>
      </div>

      {showForm && (
        <div className="alert-form-overlay">
          <div className="alert-form-card">
            <div className="alert-form-header">
              <div>
                <h2>Create Alert</h2>
                <p>Configure a new monitoring rule.</p>
              </div>

              <button
                className="icon-button"
                onClick={() => setShowForm(false)}
                aria-label="Close form"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="name">Alert Name</label>

                <input
                  id="name"
                  name="name"
                  type="text"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="e.g. High Error Rate"
                />
              </div>

              <div className="form-group">
                <label htmlFor="service">Service</label>

                <select
                  id="service"
                  name="service"
                  value={formData.service}
                  onChange={handleChange}
                >
                  <option value="ALL">All Services</option>
                  <option value="billing-api">billing-api</option>
                  <option value="payment-service">
                    payment-service
                  </option>
                  <option value="order-service">
                    order-service
                  </option>
                  <option value="auth-service">
                    auth-service
                  </option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="condition">Condition</label>

                <select
                  id="condition"
                  name="condition"
                  value={formData.condition}
                  onChange={handleConditionChange}
                >
                  <option value="Error rate">Error rate</option>
                  <option value="Response time">
                    Response time
                  </option>
                  <option value="Log count">Log count</option>
                  <option value="Database connection">
                    Database connection
                  </option>
                </select>

                <small className="form-help">
                  {conditionConfig[formData.condition]?.description}
                </small>
              </div>

              <div className="form-group">
                <label htmlFor="threshold">Threshold</label>

                <div className="threshold-input-wrapper">
                  <input
                    id="threshold"
                    name="threshold"
                    type="number"
                    min="0"
                    value={formData.threshold}
                    onChange={handleChange}
                    placeholder={
                      conditionConfig[formData.condition]?.placeholder
                    }
                  />

                  {conditionConfig[formData.condition]?.unit && (
                    <span className="threshold-unit">
                      {conditionConfig[formData.condition].unit}
                    </span>
                  )}
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="severity">Severity</label>

                <select
                  id="severity"
                  name="severity"
                  value={formData.severity}
                  onChange={handleChange}
                >
                  <option value="CRITICAL">CRITICAL</option>
                  <option value="HIGH">HIGH</option>
                  <option value="MEDIUM">MEDIUM</option>
                  <option value="LOW">LOW</option>
                </select>

                <small className="form-help">
                  {severityConfig[formData.severity]?.description}
                </small>
              </div>

              <label className="toggle-row">
                <input
                  type="checkbox"
                  name="enabled"
                  checked={formData.enabled}
                  onChange={handleChange}
                />

                <span>
                  <strong>Enable alert</strong>
                  <small>
                    Start monitoring this rule immediately.
                  </small>
                </span>
              </label>

              <div className="form-actions">
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => setShowForm(false)}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-button"
                >
                  <Plus size={18} />
                  Create Alert
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <section className="alerts-section">
        <div className="section-header">
          <div>
            <h2>Alert Activity</h2>
            <p>
              Current alert rules and their monitoring status.
            </p>
          </div>
        </div>

        {loading ? (
          <div className="alerts-empty-state">
            <div className="loading-spinner"></div>
            <p>Loading alerts...</p>
          </div>
        ) : alerts.length === 0 ? (
          <div className="alerts-empty-state">
            <Bell size={36} />
            <h3>No alerts configured</h3>
            <p>
              Create your first alert rule to start monitoring
              your services.
            </p>

            <button
              className="primary-button"
              onClick={() => setShowForm(true)}
            >
              <Plus size={18} />
              Create Alert
            </button>
          </div>
        ) : (
          <div className="alerts-list">
            {alerts.map((alert) => (
              <div
                className={`alert-item ${
                  alert.triggered ? "alert-triggered" : ""
                }`}
                key={alert.id}
              >
                <div className="alert-main">
                  <div className="alert-icon">
                    {alert.triggered ? (
                      <AlertTriangle size={20} />
                    ) : (
                      <Bell size={20} />
                    )}
                  </div>

                  <div className="alert-content">
                    <div className="alert-title-row">
                      <h3>{alert.name}</h3>

                      <span
                        className={`severity-badge severity-${String(
                          alert.severity || "LOW"
                        ).toLowerCase()}`}
                      >
                        {alert.severity}
                      </span>
                    </div>

                    <div className="alert-details">
                      <span>
                        Service:{" "}
                        <strong>
                          {alert.service || "All Services"}
                        </strong>
                      </span>

                      <span>
                        Condition:{" "}
                        <strong>{alert.condition}</strong>
                      </span>

                      <span>
                        Threshold:{" "}
                        <strong>{alert.threshold}</strong>
                      </span>
                    </div>

                    {alert.lastTriggered && (
                      <div className="alert-last-triggered">
                        Last triggered:{" "}
                        {new Date(
                          alert.lastTriggered
                        ).toLocaleString()}
                      </div>
                    )}
                  </div>
                </div>

                <div className="alert-actions">
                  <span
                    className={`status-badge ${
                      alert.triggered
                        ? "status-triggered"
                        : alert.enabled
                        ? "status-active"
                        : "status-disabled"
                    }`}
                  >
                    {alert.triggered
                      ? "Triggered"
                      : alert.enabled
                      ? "Active"
                      : "Disabled"}
                  </span>

                  <button
                    className="delete-alert-button"
                    onClick={() =>
                      handleDeleteAlert(alert.id)
                    }
                    title="Delete alert"
                    aria-label={`Delete ${alert.name}`}
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

export default Alerts;