import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Mail, Lock, ArrowRight, Sparkles, Sun, Moon } from "lucide-react";

import api from "../services/api";
import { useTheme } from "../context/ThemeContext";

function LoginPage() {
  const navigate = useNavigate();
  const { theme, toggleTheme } = useTheme();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();

    setLoading(true);
    setError("");

    try {
      const response = await api.post("/auth/login", {
        email,
        password,
      });

      localStorage.setItem("token", response.data.token);

      navigate("/dashboard");
    } catch (error) {
      console.error("Login error:", error);

      setError("Invalid email or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <button
        type="button"
        className="login-theme-toggle"
        onClick={toggleTheme}
        aria-label="Toggle theme"
        title={theme === "light" ? "Enable dark mode" : "Enable light mode"}
      >
        {theme === "light" ? <Moon size={19} /> : <Sun size={19} />}
      </button>
      
      <div className="login-card">
        <div className="login-brand">
          <div className="login-logo">
            <Sparkles size={28} />
          </div>

          <div>
            <h1>TeamSphere</h1>
            <p>Your team. One workspace.</p>
          </div>
        </div>

        <div className="login-heading">
          <span>WELCOME BACK</span>
          <h2>Sign in to your workspace</h2>
          <p>Manage your team, tasks, events and more.</p>
        </div>

        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="login-field">
            <label>Email</label>

            <div className="input-wrapper">
              <Mail size={18} />

              <input
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
              />
            </div>
          </div>

          <div className="login-field">
            <label>Password</label>

            <div className="input-wrapper">
              <Lock size={18} />

              <input
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
              />
            </div>
          </div>

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}

            {!loading && <ArrowRight size={18} />}
          </button>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
