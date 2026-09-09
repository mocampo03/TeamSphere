import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Users,
  ListTodo,
  Clock3,
  CircleDotDashed,
  CheckCircle2,
  CalendarDays,
  ArrowUpRight,
  Sparkles,
} from "lucide-react";

import api from "../services/api";

function DashboardPage() {
  const navigate = useNavigate();

  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const response = await api.get("/dashboard");

        setDashboardData(response.data);
      } catch (error) {
        console.error("Error loading dashboard:", error);

        setError("Unable to load dashboard data.");
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-loading">Loading your workspace...</div>
      </div>
    );
  }

  if (error || !dashboardData) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-error">
          {error || "Unable to load dashboard."}
        </div>
      </div>
    );
  }

  const completionPercentage =
    dashboardData.totalTasks > 0
      ? Math.round(
          (dashboardData.completedTasks / dashboardData.totalTasks) * 100,
        )
      : 0;

  const taskDistribution = [
    {
      label: "To Do",
      value: dashboardData.todoTasks,
      className: "todo",
    },
    {
      label: "In Progress",
      value: dashboardData.inProgressTasks,
      className: "progress",
    },
    {
      label: "Completed",
      value: dashboardData.completedTasks,
      className: "completed",
    },
  ];

  return (
    <div className="dashboard-page">
      <section className="dashboard-header">
        <div>
          <div className="dashboard-eyebrow">
            <Sparkles size={16} />
            TEAM OVERVIEW
          </div>

          <h1>Welcome to TeamSphere 👋</h1>

          <p>Here is what is happening across your workspace today.</p>
        </div>

        <button
          className="dashboard-action"
          onClick={() => navigate("/members")}
        >
          View workspace
          <ArrowUpRight size={18} />
        </button>
      </section>

      <section className="stats-grid">
        <article className="stat-card">
          <div className="stat-icon members-icon">
            <Users size={22} />
          </div>

          <div className="stat-content">
            <span>Total Members</span>
            <strong>{dashboardData.totalMembers}</strong>
            <small>Your active team</small>
          </div>
        </article>

        <article className="stat-card">
          <div className="stat-icon tasks-icon">
            <ListTodo size={22} />
          </div>

          <div className="stat-content">
            <span>Total Tasks</span>
            <strong>{dashboardData.totalTasks}</strong>
            <small>Across your workspace</small>
          </div>
        </article>

        <article className="stat-card">
          <div className="stat-icon events-icon">
            <CalendarDays size={22} />
          </div>

          <div className="stat-content">
            <span>Total Events</span>
            <strong>{dashboardData.totalEvents}</strong>
            <small>Scheduled activities</small>
          </div>
        </article>

        <article className="stat-card">
          <div className="stat-icon completed-icon">
            <CheckCircle2 size={22} />
          </div>

          <div className="stat-content">
            <span>Completion Rate</span>
            <strong>{completionPercentage}%</strong>
            <small>Tasks completed</small>
          </div>
        </article>
      </section>

      <section className="dashboard-content-grid">
        <article className="dashboard-panel task-overview-panel">
          <div className="panel-header">
            <div>
              <span className="panel-label">TASK MANAGEMENT</span>
              <h2>Task Overview</h2>
            </div>

            <ListTodo size={22} />
          </div>

          <div className="task-progress-container">
            <div className="task-progress-info">
              <div>
                <span>Overall progress</span>
                <strong>{completionPercentage}% completed</strong>
              </div>

              <strong>{dashboardData.totalTasks} tasks</strong>
            </div>

            <div className="progress-bar">
              <div
                className="progress-bar-fill"
                style={{ width: `${completionPercentage}%` }}
              />
            </div>
          </div>

          <div className="task-distribution">
            {taskDistribution.map((task) => (
              <div className="distribution-item" key={task.label}>
                <div className="distribution-info">
                  <span className={`status-dot ${task.className}`} />

                  <span>{task.label}</span>
                </div>

                <strong>{task.value}</strong>
              </div>
            ))}
          </div>
        </article>

        <article className="dashboard-panel productivity-panel">
          <div className="panel-header">
            <div>
              <span className="panel-label">PRODUCTIVITY</span>
              <h2>Workspace Activity</h2>
            </div>

            <Sparkles size={22} />
          </div>

          <div className="activity-score">
            <div className="score-circle">
              <strong>{completionPercentage}%</strong>
              <span>Progress</span>
            </div>

            <div className="score-text">
              <h3>Keep the momentum going!</h3>

              <p>
                Your team currently has{" "}
                <strong>{dashboardData.inProgressTasks}</strong> tasks actively
                in progress.
              </p>
            </div>
          </div>
        </article>
      </section>

      <section className="quick-insights">
        <article className="insight-card">
          <div className="insight-icon">
            <Clock3 size={22} />
          </div>

          <div>
            <span>Pending work</span>
            <strong>{dashboardData.todoTasks} tasks need attention</strong>
          </div>
        </article>

        <article className="insight-card">
          <div className="insight-icon">
            <CircleDotDashed size={22} />
          </div>

          <div>
            <span>Currently active</span>
            <strong>
              {dashboardData.inProgressTasks} tasks are in progress
            </strong>
          </div>
        </article>

        <article className="insight-card">
          <div className="insight-icon">
            <CheckCircle2 size={22} />
          </div>

          <div>
            <span>Completed work</span>
            <strong>
              {dashboardData.completedTasks} tasks successfully finished
            </strong>
          </div>
        </article>
      </section>
    </div>
  );
}

export default DashboardPage;
