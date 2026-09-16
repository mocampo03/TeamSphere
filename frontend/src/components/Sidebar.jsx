import {
  LayoutDashboard,
  Users,
  CheckSquare,
  CalendarDays,
  BarChart3,
  Bot,
  Settings,
  LogOut,
  ChevronLeft,
  ChevronRight,
  Moon,
  Sun,
  Languages,
} from "lucide-react";

import { NavLink, useNavigate } from "react-router-dom";
import { useState } from "react";

import { useTheme } from "../context/ThemeContext";
import { useLanguage } from "../context/LanguageContext";

function Sidebar({ isCollapsed, setIsCollapsed }) {
  const [showSettings, setShowSettings] = useState(false);

  const { theme, toggleTheme } = useTheme();
  const { language, changeLanguage, t } = useLanguage();

  const navigate = useNavigate();

  const navigationItems = [
    {
      name: t("sidebar.dashboard"),
      path: "/dashboard",
      icon: LayoutDashboard,
    },
    {
      name: t("sidebar.members"),
      path: "/members",
      icon: Users,
    },
    {
      name: t("sidebar.tasks"),
      path: "/tasks",
      icon: CheckSquare,
    },
    {
      name: t("sidebar.events"),
      path: "/events",
      icon: CalendarDays,
    },
    {
      name: t("sidebar.reports"),
      path: "/reports",
      icon: BarChart3,
    },
    {
      name: t("sidebar.aiAssistant"),
      path: "/ai-assistant",
      icon: Bot,
    },
  ];

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  const handleLanguageChange = (event) => {
    changeLanguage(event.target.value);
  };

  return (
    <aside className={`sidebar ${isCollapsed ? "collapsed" : ""}`}>
      <div className="sidebar-header">
        <div className="logo-container">
          <img
            src="/images/TeamSphereLogo.png"
            alt="TeamSphere"
            className="teamsphere-logo"
          />

          {!isCollapsed && (
            <div className="workspace-label">
              <span>{t("sidebar.workspace")}</span>
            </div>
          )}

          <button
            className="collapse-button"
            onClick={() => setIsCollapsed((previous) => !previous)}
            aria-label={isCollapsed ? "Expand sidebar" : "Collapse sidebar"}
            title={isCollapsed ? "Expand sidebar" : "Collapse sidebar"}
          >
            {isCollapsed ? (
              <ChevronRight size={16} />
            ) : (
              <ChevronLeft size={16} />
            )}
          </button>
        </div>
      </div>

      <nav className="sidebar-navigation">
        {!isCollapsed && <span className="navigation-label">WORKSPACE</span>}

        {navigationItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `navigation-item ${isActive ? "active" : ""}`
              }
              title={isCollapsed ? item.name : ""}
            >
              <Icon size={20} />

              {!isCollapsed && <span>{item.name}</span>}
            </NavLink>
          );
        })}
      </nav>

      <div className="sidebar-footer">
        <button
          className="navigation-item"
          onClick={() => setShowSettings(!showSettings)}
          title={isCollapsed ? t("sidebar.settings") : ""}
        >
          <Settings size={20} />

          {!isCollapsed && <span>{t("sidebar.settings")}</span>}
        </button>

        {showSettings && !isCollapsed && (
          <div className="settings-panel">
            <div className="settings-panel-header">
              <strong>{t("sidebar.appearance")}</strong>
              <span>{t("sidebar.customizeWorkspace")}</span>
            </div>

            <button className="theme-option" onClick={toggleTheme}>
              {theme === "light" ? <Moon size={18} /> : <Sun size={18} />}

              <div>
                <strong>
                  {theme === "light"
                    ? t("sidebar.darkMode")
                    : t("sidebar.lightMode")}
                </strong>

                <span>{t("sidebar.switchAppearance")}</span>
              </div>
            </button>

            <div className="language-option">
              <Languages size={18} />

              <div>
                <strong>{t("sidebar.language")}</strong>

                <select
                  value={language}
                  onChange={handleLanguageChange}
                  aria-label={t("sidebar.language")}
                >
                  <option value="es">{t("sidebar.spanish")}</option>
                  <option value="en">{t("sidebar.english")}</option>
                </select>
              </div>
            </div>
          </div>
        )}

        <button
          className="navigation-item logout-button"
          onClick={handleLogout}
          title={isCollapsed ? t("sidebar.logout") : ""}
        >
          <LogOut size={20} />

          {!isCollapsed && <span>{t("sidebar.logout")}</span>}
        </button>
      </div>
    </aside>
  );
}

export default Sidebar;
