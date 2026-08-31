import {
  Activity,
  ArrowRight,
  BarChart3,
  Search,
  ShieldAlert,
} from "lucide-react";
import { Link } from "react-router-dom";

function Landing() {
  return (
    <div className="landing-page">
      <header className="landing-navbar">
        <div className="landing-brand">
          <img src="/logo.png" alt="LogStream logo" />
          <span>LogStream</span>
        </div>

        <Link to="/dashboard" className="landing-nav-button">
          Open Dashboard
          <ArrowRight size={16} />
        </Link>
      </header>

      <main>
        <section className="landing-hero">
          <div className="landing-badge">
            <Activity size={15} />
            Distributed Log Analytics
          </div>

          <h1>
            Monitor. Analyze.
            <span> Act.</span>
          </h1>

          <p>
            Search, analyze, and monitor application logs across
            distributed services from one centralized platform.
          </p>

          <div className="landing-actions">
            <Link to="/dashboard" className="primary-landing-button">
              Open Dashboard
              <ArrowRight size={17} />
            </Link>

            <Link to="/search" className="secondary-landing-button">
              Search Logs
            </Link>
          </div>
        </section>

        <section className="landing-features">
          <div className="landing-feature-card">
            <div className="feature-icon">
              <Search size={21} />
            </div>

            <h3>Search Logs</h3>

            <p>
              Find logs quickly using keywords, services, hosts,
              and log levels.
            </p>
          </div>

          <div className="landing-feature-card">
            <div className="feature-icon">
              <BarChart3 size={21} />
            </div>

            <h3>Analyze Activity</h3>

            <p>
              Understand log volumes and system activity through
              analytics and visualizations.
            </p>
          </div>

          <div className="landing-feature-card">
            <div className="feature-icon">
              <ShieldAlert size={21} />
            </div>

            <h3>Monitor Alerts</h3>

            <p>
              Configure monitoring rules for important log patterns
              and system events.
            </p>
          </div>
        </section>

        <section className="landing-bottom">
          <div>
            <span>LOGSTREAM</span>
            <h2>
              One place for your distributed
              <br />
              application logs.
            </h2>
          </div>

          <Link to="/dashboard" className="landing-bottom-button">
            Explore LogStream
            <ArrowRight size={16} />
          </Link>
        </section>
      </main>

      <footer className="landing-footer">
        <span>LogStream</span>
        <span>Distributed Log Analytics & Alerting Platform</span>
      </footer>
    </div>
  );
}

export default Landing;