import { useEffect, useState } from "react";
import ReactECharts from "echarts-for-react";
import {
  getLogLevelAnalytics,
  getServiceAnalytics,
} from "../../services/analyticsService";

const fallbackLevels = [
  { value: 7240, name: "INFO" },
  { value: 324, name: "ERROR" },
  { value: 918, name: "WARN" },
];

const fallbackServices = [
  { value: 4200, name: "billing-api" },
  { value: 3100, name: "payment-service" },
  { value: 2450, name: "order-service" },
  { value: 2732, name: "auth-service" },
];

function normalizeAnalytics(data, fallback) {
  if (!Array.isArray(data) || data.length === 0) {
    return fallback;
  }

  return data.map((item) => ({
    name: item.name,
    value: Number(item.value) || 0,
  }));
}

function LogAnalytics() {
  const [levelData, setLevelData] = useState([]);
  const [serviceData, setServiceData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [usingFallback, setUsingFallback] = useState(false);

  useEffect(() => {
    let cancelled = false;

    async function loadAnalytics() {
      try {
        const [levels, services] = await Promise.all([
          getLogLevelAnalytics(),
          getServiceAnalytics(),
        ]);

        if (!cancelled) {
          setLevelData(normalizeAnalytics(levels, fallbackLevels));
          setServiceData(
            normalizeAnalytics(services, fallbackServices)
          );
          setUsingFallback(false);
          setLoading(false);
        }
      } catch (error) {
        console.error("Analytics API error:", error);

        if (!cancelled) {
          setLevelData(fallbackLevels);
          setServiceData(fallbackServices);
          setUsingFallback(true);
          setLoading(false);
        }
      }
    }

    loadAnalytics();

    return () => {
      cancelled = true;
    };
  }, []);

  const levelOption = {
    tooltip: {
      trigger: "item",
    },

    legend: {
      bottom: 0,
    },

    series: [
      {
        name: "Log Level",
        type: "pie",
        radius: ["45%", "70%"],
        avoidLabelOverlap: true,

        itemStyle: {
          borderRadius: 6,
          borderWidth: 2,
        },

        label: {
          show: true,
          formatter: "{b}: {c}",
        },

        data: levelData,
      },
    ],
  };

  const serviceOption = {
    tooltip: {
      trigger: "axis",
    },

    grid: {
      left: 50,
      right: 20,
      top: 30,
      bottom: 70,
    },

    xAxis: {
      type: "category",
      data: serviceData.map((item) => item.name),

      axisLabel: {
        rotate: 20,
      },
    },

    yAxis: {
      type: "value",
    },

    series: [
      {
        name: "Logs",
        type: "bar",
        data: serviceData.map((item) => item.value),
        barMaxWidth: 45,
      },
    ],
  };

  return (
    <section className="analytics-section">
      <div className="section-header">
        <div>
          <h2>Log Analytics</h2>
          <p>
            Overview of log distribution across the platform
          </p>
        </div>

        {usingFallback && !loading && (
          <span className="analytics-status">
            Demo data
          </span>
        )}
      </div>

      {loading ? (
        <div className="analytics-loading">
          Loading analytics...
        </div>
      ) : (
        <div className="analytics-grid">
          <div className="analytics-card">
            <h3>Logs by Level</h3>

            <ReactECharts
              option={levelOption}
              style={{
                height: "320px",
                width: "100%",
              }}
            />
          </div>

          <div className="analytics-card">
            <h3>Logs by Service</h3>

            <ReactECharts
              option={serviceOption}
              style={{
                height: "320px",
                width: "100%",
              }}
            />
          </div>
        </div>
      )}
    </section>
  );
}

export default LogAnalytics;