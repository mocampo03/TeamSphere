import {
  LayoutDashboard,
  Users,
  CheckSquare,
  CalendarDays,
  BarChart3,
  Settings,
  LogOut,
  ChevronLeft,
} from "lucide-react";

import { NavLink, useNavigate } from "react-router-dom";
import { useState } from "react";

import { useTheme } from "../context/ThemeContext";

function Sidebar() {
  const [showSettings, setShowSettings] = useState(false);

  const { theme, toggleTheme } = useTheme();
  const navigationItems = [
    {
      name: "Dashboard",
      path: "/dashboard",
      icon: LayoutDashboard,
    },
    {
      name: "Members",
      path: "/members",
      icon: Users,
    },
    {
      name: "Tasks",
      path: "/tasks",
      icon: CheckSquare,
    },
    {
      name: "Events",
      path: "/events",
      icon: CalendarDays,
    },
    {
      name: "Reports",
      path: "/reports",
      icon: BarChart3,
    },
  ];

  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="logo-container">
          <div className="logo-icon">TS</div>

          <div>
            <h1>TeamSphere</h1>
            <span>Workspace</span>
          </div>
        </div>

        <button className="collapse-button">
          <ChevronLeft size={18} />
        </button>
      </div>

      <nav className="sidebar-navigation">
        <span className="navigation-label">WORKSPACE</span>

        {navigationItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.name}
              to={item.path}
              className={({ isActive }) =>
                `navigation-item ${isActive ? "active" : ""}`
              }
            >
              <Icon size={20} />

              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="sidebar-footer">
        <button
          className="navigation-item"
          onClick={() => setShowSettings(!showSettings)}
        >
          <Settings size={20} />
          <span>Settings</span>
        </button>

        {showSettings && (
          <div className="settings-panel">
            <div className="settings-panel-header">
              <strong>Appearance</strong>
              <span>Customize your workspace</span>
            </div>

            <button className="theme-option" onClick={toggleTheme}>
              <span>{theme === "light" ? "🌙" : "☀️"}</span>

              <div>
                <strong>
                  {theme === "light" ? "Dark mode" : "Light mode"}
                </strong>

                <span>Switch workspace appearance</span>
              </div>
            </button>
          </div>
        )}

        <button
          className="navigation-item logout-button"
          onClick={handleLogout}
        >
          <LogOut size={20} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
}

export default Sidebar;
