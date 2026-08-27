import { Activity } from "lucide-react";

import StatCard from "../components/dashboard/StatCard";
import RecentLogs from "../components/dashboard/RecentLogs";
import { mockLogs } from "../data/mockLogs";

function Dashboard() {
  return (
    <div className="dashboard-page">
      <div className="page-heading">
        <div>
          <span className="page-eyebrow">OVERVIEW</span>
          <h1>Dashboard</h1>
          <p>
            Monitor application logs and system activity across
            your distributed services.
          </p>
        </div>

        <div className="live-status">
          <Activity size={15} />
          Live monitoring
        </div>
      </div>

      <div className="stats-grid">
        <StatCard
          label="Total Logs"
          value="12,482"
          trend="+8.4%"
          description="vs. previous hour"
          trendDirection="up"
        />

        <StatCard
          label="Errors"
          value="324"
          trend="+2.1%"
          description="vs. previous hour"
          trendDirection="up"
        />

        <StatCard
          label="Warnings"
          value="681"
          trend="-4.6%"
          description="vs. previous hour"
          trendDirection="down"
        />

        <StatCard
          label="Active Services"
          value="8"
          trend="100%"
          description="services healthy"
          trendDirection="up"
        />
      </div>

      <RecentLogs logs={mockLogs} />
    </div>
  );
}

export default Dashboard;