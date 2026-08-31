import { Bell, Moon, Sun  } from "lucide-react";

function Navbar({ darkMode, setDarkMode }) {
  return (
    <header className="navbar">
      <div className="navbar-brand">
        <strong>LogStream</strong>
        <span>Distributed Log Analytics</span>
      </div>

      <div className="navbar-actions">
        <button
          className="theme-toggle"
          type="button"
          onClick={() => setDarkMode(!darkMode)}
          aria-label="Toggle theme"
          title={darkMode ? "Switch to light mode" : "Switch to dark mode"}
        >
          {darkMode ? <Sun size={18} /> : <Moon size={18} />}
        </button>

        <button className="icon-button" aria-label="Notifications">
          <Bell size={19} />
        </button>

        <div className="system-status">
          <span className="status-dot"></span>

          <div>
            <span>System</span>
            <small>Connected</small>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Navbar;