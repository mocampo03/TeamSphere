import { useEffect, useState } from "react";
import {
  Search,
  Bell,
  ChevronDown,
  Sun,
  Moon,
  LogOut,
  Users,
  CheckSquare,
  CalendarDays,
  BarChart3,
} from "lucide-react";

import { useNavigate } from "react-router-dom";

import { useTheme } from "../context/ThemeContext";
import { getCurrentUser } from "../services/auth";
import api from "../services/api";

function Topbar() {
  const navigate = useNavigate();

  const { theme, toggleTheme } = useTheme();

  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  const [searchText, setSearchText] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [searching, setSearching] = useState(false);

  const user = getCurrentUser();

  const userName = user?.name || "User";
  const userRole = user?.role || "USER";

  const initial = userName.charAt(0).toUpperCase();

  useEffect(() => {
    const search = async () => {
      const query = searchText.trim();

      if (!query) {
        setSearchResults([]);
        setShowSearchResults(false);
        return;
      }

      try {
        setSearching(true);

        const response = await api.get("/search", {
          params: {
            query,
          },
        });

        setSearchResults(response.data);
        setShowSearchResults(true);
      } catch (error) {
        console.error("Error searching:", error);
        setSearchResults([]);
      } finally {
        setSearching(false);
      }
    };

    const timeout = setTimeout(search, 300);

    return () => clearTimeout(timeout);
  }, [searchText]);

  const handleSearchKeyDown = (event) => {
    if (event.key === "Enter" && searchResults.length > 0) {
      navigate(searchResults[0].path);
      setSearchText("");
      setShowSearchResults(false);
    }

    if (event.key === "Escape") {
      setShowSearchResults(false);
    }
  };

  const handleResultClick = (result) => {
    navigate(result.path);
    setSearchText("");
    setShowSearchResults(false);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  const getResultIcon = (type) => {
    if (type === "Member") return <Users size={17} />;
    if (type === "Task") return <CheckSquare size={17} />;
    if (type === "Event") return <CalendarDays size={17} />;
    if (type === "Report") return <BarChart3 size={17} />;

    return <Search size={17} />;
  };

  return (
    <header className="topbar">
      <div className="topbar-search-wrapper">
        <div className="topbar-search">
          <Search size={20} />

          <input
            type="text"
            placeholder="Search anything..."
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
            onKeyDown={handleSearchKeyDown}
            onFocus={() => {
              if (searchResults.length > 0) {
                setShowSearchResults(true);
              }
            }}
          />
        </div>

        {showSearchResults && (
          <div className="search-results-dropdown">
            {searching ? (
              <div className="search-result-empty">Searching...</div>
            ) : searchResults.length > 0 ? (
              searchResults.map((result) => (
                <button
                  key={`${result.type}-${result.id}`}
                  className="search-result-item"
                  onClick={() => handleResultClick(result)}
                >
                  <div className="search-result-icon">
                    {getResultIcon(result.type)}
                  </div>

                  <div className="search-result-information">
                    <strong>{result.title}</strong>
                    <span>
                      {result.type}
                      {result.subtitle ? ` · ${result.subtitle}` : ""}
                    </span>
                  </div>
                </button>
              ))
            ) : (
              <div className="search-result-empty">No results found</div>
            )}
          </div>
        )}
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
