import { Bell, UserCircle } from "lucide-react";

function Navbar() {
  return (
    <header className="navbar">
      <div>
        <span className="navbar-label">Distributed Log Analytics</span>
      </div>

      <div className="navbar-actions">
        <button className="icon-button" aria-label="Notifications">
          <Bell size={19} />
        </button>

        <div className="user-profile">
          <UserCircle size={22} />

          <div>
            <span>Nikkiraj4</span>
            <small>Frontend Developer</small>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Navbar;