import { useEffect, useState } from "react";
import { CalendarDays, Edit3, Plus, Trash2, X } from "lucide-react";
import api from "../services/api";
import { getCurrentUser } from "../services/auth";

function EventsPage() {
  const user = getCurrentUser();
  const isAdmin = user?.role === "ADMIN";
  const organizationId = user?.organizationId;

  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [editingEvent, setEditingEvent] = useState(null);
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    title: "",
    description: "",
    startDate: "",
    endDate: "",
    organizationId: organizationId || null,
  });

  useEffect(() => {
    loadEvents();
  }, []);

  async function loadEvents() {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/events");
      setEvents(response.data);
    } catch (err) {
      console.error("Error loading events:", err);
      setError("Unable to load events.");
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingEvent(null);

    setForm({
      title: "",
      description: "",
      startDate: "",
      endDate: "",
      organizationId: organizationId || null,
    });

    setShowModal(true);
  }

  function openEditModal(event) {
    setEditingEvent(event);

    setForm({
      title: event.title || "",
      description: event.description || "",
      startDate: formatForInput(event.startDate),
      endDate: formatForInput(event.endDate),
      organizationId: event.organizationId || organizationId || null,
    });

    setShowModal(true);
  }

  function closeModal() {
    if (saving) return;

    setShowModal(false);
    setEditingEvent(null);
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

    if (!organizationId) {
      setError("Unable to determine the user's organization.");
      return;
    }

    if (new Date(form.endDate) < new Date(form.startDate)) {
      setError("End date cannot be earlier than start date.");
      return;
    }

    try {
      setSaving(true);
      setError("");

      const payload = {
        title: form.title,
        description: form.description,
        startDate: form.startDate,
        endDate: form.endDate,
        organizationId,
      };

      if (editingEvent) {
        await api.put(`/events/${editingEvent.id}`, payload);
      } else {
        await api.post("/events", payload);
      }

      closeModal();
      await loadEvents();
    } catch (err) {
      console.error("Error saving event:", err);
      setError(err.response?.data?.message || "Unable to save event.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(eventId) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this event?",
    );

    if (!confirmed) return;

    try {
      setError("");
      await api.delete(`/events/${eventId}`);
      await loadEvents();
    } catch (err) {
      console.error("Error deleting event:", err);
      setError(err.response?.data?.message || "Unable to delete event.");
    }
  }

  function formatDate(dateValue) {
    if (!dateValue) return "—";

    return new Date(dateValue).toLocaleString([], {
      dateStyle: "medium",
      timeStyle: "short",
    });
  }

  function formatForInput(dateValue) {
    if (!dateValue) return "";

    return dateValue.slice(0, 16);
  }

  return (
    <div className="page-container events-page">
      <div className="page-header">
        <div>
          <h1>Events</h1>
          <p>Manage your organization's events.</p>
        </div>

        {isAdmin && (
          <button className="primary-button" onClick={openCreateModal}>
            <Plus size={17} />
            New event
          </button>
        )}
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="empty-state">
          <p>Loading events...</p>
        </div>
      ) : events.length === 0 ? (
        <div className="empty-state">
          <CalendarDays size={40} />
          <h3>No events yet</h3>
          <p>Create your first organization event to get started.</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Event</th>
                <th>Start date</th>
                <th>End date</th>
                {isAdmin && <th>Actions</th>}
              </tr>
            </thead>

            <tbody>
              {events.map((event) => (
                <tr key={event.id}>
                  <td>
                    <strong>{event.title}</strong>

                    {event.description && (
                      <span className="table-description">
                        {event.description}
                      </span>
                    )}
                  </td>

                  <td>{formatDate(event.startDate)}</td>
                  <td>{formatDate(event.endDate)}</td>

                  {isAdmin && (
                    <td>
                      <div className="table-actions">
                        <button
                          className="icon-button"
                          title="Edit event"
                          onClick={() => openEditModal(event)}
                        >
                          <Edit3 size={16} />
                        </button>

                        <button
                          className="icon-button danger"
                          title="Delete event"
                          onClick={() => handleDelete(event.id)}
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
                <h2>{editingEvent ? "Edit event" : "Create event"}</h2>

                <p>
                  {editingEvent
                    ? "Update the event information."
                    : "Add a new event to your organization."}
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
                  placeholder="Team meeting"
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
                  placeholder="Describe the event..."
                />
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label htmlFor="startDate">Start date</label>

                  <input
                    id="startDate"
                    name="startDate"
                    type="datetime-local"
                    value={form.startDate}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="endDate">End date</label>

                  <input
                    id="endDate"
                    name="endDate"
                    type="datetime-local"
                    value={form.endDate}
                    onChange={handleChange}
                    required
                  />
                </div>
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
                    : editingEvent
                      ? "Update event"
                      : "Create event"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default EventsPage;
