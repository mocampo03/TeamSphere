import { useState } from "react";
import { Search, Bell, ChevronDown, Sun, Moon, LogOut } from "lucide-react";

import { useNavigate } from "react-router-dom";

import { useTheme } from "../context/ThemeContext";
import { getCurrentUser } from "../services/auth";

function Topbar() {
  const navigate = useNavigate();

  const { theme, toggleTheme } = useTheme();

  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  const user = getCurrentUser();

  const userName = user?.name || "User";
  const userRole = user?.role || "USER";

  const initial = userName.charAt(0).toUpperCase();

  const handleLogout = () => {
    localStorage.removeItem("token");

    navigate("/login");
  };

  return (
    <header className="topbar">
      <div className="topbar-search">
        <Search size={20} />

        <input type="text" placeholder="Search anything..." />
      </div>

      <div className="topbar-actions">
        <button
          className="theme-toggle-button"
          onClick={toggleTheme}
          aria-label="Toggle dark mode"
          title="Toggle theme"
        >
          {theme === "light" ? <Moon size={20} /> : <Sun size={20} />}
        </button>

        <div className="notification-wrapper">
          <button
            className="notification-button"
            onClick={() => {
              setShowNotifications(!showNotifications);
              setShowUserMenu(false);
            }}
          >
            <Bell size={21} />

            <span className="notification-dot"></span>
          </button>

          {showNotifications && (
            <div className="topbar-dropdown notification-dropdown">
              <div className="dropdown-header">
                <strong>Notifications</strong>
              </div>

              <div className="empty-notifications">
                <Bell size={22} />

                <p>No new notifications</p>

                <span>You're all caught up!</span>
              </div>
            </div>
          )}
        </div>

        <div className="user-menu-wrapper">
          <button
            className="user-profile"
            onClick={() => {
              setShowUserMenu(!showUserMenu);
              setShowNotifications(false);
            }}
          >
            <div className="user-avatar">{initial}</div>

            <div className="user-information">
              <span className="user-name">{userName}</span>

              <span className="user-role">{userRole}</span>
            </div>

            <ChevronDown size={18} />
          </button>

          {showUserMenu && (
            <div className="topbar-dropdown user-dropdown">
              <div className="user-dropdown-info">
                <strong>{userName}</strong>

                <span>{user?.email || userRole}</span>
              </div>

              <button className="user-dropdown-logout" onClick={handleLogout}>
                <LogOut size={17} />
                Logout
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

export default Topbar;
