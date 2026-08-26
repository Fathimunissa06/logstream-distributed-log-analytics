import { ArrowDown, ArrowUp } from "lucide-react";

function StatCard({
  label,
  value,
  description,
  trend,
  trendDirection = "up",
}) {
  const TrendIcon = trendDirection === "up" ? ArrowUp : ArrowDown;

  return (
    <div className="stat-card">
      <div className="stat-card-header">
        <span>{label}</span>
      </div>

      <div className="stat-card-value">{value}</div>

      <div className="stat-card-footer">
        <span
          className={`stat-trend ${
            trendDirection === "up" ? "positive" : "negative"
          }`}
        >
          <TrendIcon size={13} />
          {trend}
        </span>

        <span className="stat-description">{description}</span>
      </div>
    </div>
  );
}

export default StatCard;