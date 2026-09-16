import { useEffect, useState } from "react";
import { Edit3, FileText, Plus, Trash2, X } from "lucide-react";
import api from "../services/api";
import { getCurrentUser } from "../services/auth";

function ReportsPage() {
  const user = getCurrentUser();
  const isAdmin = user?.role === "ADMIN";

  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [editingReport, setEditingReport] = useState(null);
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    title: "",
    description: "",
  });

  useEffect(() => {
    loadReports();
  }, []);

  async function loadReports() {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/reports");
      setReports(response.data);
    } catch (err) {
      console.error("Error loading reports:", err);
      setError("Unable to load reports.");
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingReport(null);

    setForm({
      title: "",
      description: "",
    });

    setShowModal(true);
  }

  function openEditModal(report) {
    setEditingReport(report);

    setForm({
      title: report.title || "",
      description: report.description || "",
    });

    setShowModal(true);
  }

  function closeModal() {
    if (saving) return;

    setShowModal(false);
    setEditingReport(null);
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");

      const payload = {
        title: form.title,
        description: form.description,
      };

      if (editingReport) {
        await api.put(`/reports/${editingReport.id}`, payload);
      } else {
        await api.post("/reports", payload);
      }

      closeModal();
      await loadReports();
    } catch (err) {
      console.error("Error saving report:", err);
      setError(err.response?.data?.message || "Unable to save report.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(reportId) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this report?",
    );

    if (!confirmed) return;

    try {
      setError("");

      await api.delete(`/reports/${reportId}`);
      await loadReports();
    } catch (err) {
      console.error("Error deleting report:", err);
      setError(err.response?.data?.message || "Unable to delete report.");
    }
  }

  function formatDate(dateValue) {
    if (!dateValue) return "—";

    return new Date(dateValue).toLocaleString([], {
      dateStyle: "medium",
      timeStyle: "short",
    });
  }

  return (
    <div className="page-container reports-page">
      <div className="page-header">
        <div>
          <h1>Reports</h1>
          <p>View insights and reports.</p>
        </div>

        {isAdmin && (
          <button className="primary-button" onClick={openCreateModal}>
            <Plus size={17} />
            New report
          </button>
        )}
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="empty-state">
          <p>Loading reports...</p>
        </div>
      ) : reports.length === 0 ? (
        <div className="empty-state">
          <FileText size={40} />
          <h3>No reports yet</h3>
          <p>Create your first report to start documenting insights.</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Report</th>
                <th>Created at</th>
                {isAdmin && <th>Actions</th>}
              </tr>
            </thead>

            <tbody>
              {reports.map((report) => (
                <tr key={report.id}>
                  <td>
                    <strong>{report.title}</strong>

                    {report.description && (
                      <span className="table-description">
                        {report.description}
                      </span>
                    )}
                  </td>

                  <td>{formatDate(report.createdAt)}</td>

                  {isAdmin && (
                    <td>
                      <div className="table-actions">
                        <button
                          className="icon-button"
                          title="Edit report"
                          onClick={() => openEditModal(report)}
                        >
                          <Edit3 size={16} />
                        </button>

                        <button
                          className="icon-button danger"
                          title="Delete report"
                          onClick={() => handleDelete(report.id)}
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <div>
                <h2>{editingReport ? "Edit report" : "Create report"}</h2>

                <p>
                  {editingReport
                    ? "Update the report information."
                    : "Add a new report to your organization."}
                </p>
              </div>

              <button
                className="icon-button"
                onClick={closeModal}
                disabled={saving}
              >
                <X size={18} />
              </button>
            </div>

            <form className="modal-form" onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="title">Title</label>

                <input
                  id="title"
                  name="title"
                  type="text"
                  value={form.title}
                  onChange={handleChange}
                  placeholder="Monthly performance report"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="description">Description</label>

                <textarea
                  id="description"
                  name="description"
                  value={form.description}
                  onChange={handleChange}
                  placeholder="Describe the report..."
                />
              </div>

              <div className="modal-actions">
                <button
                  type="button"
                  className="secondary-button"
                  onClick={closeModal}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-button"
                  disabled={saving}
                >
                  {saving
                    ? "Saving..."
                    : editingReport
                      ? "Update report"
                      : "Create report"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default ReportsPage;
