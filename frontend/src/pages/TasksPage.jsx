import { useEffect, useState } from "react";
import { Pencil, Plus, Trash2, X } from "lucide-react";
import api from "../services/api";
import { getCurrentUser } from "../services/auth";

const emptyForm = {
  title: "",
  description: "",
  status: "TODO",
  priority: "MEDIUM",
  dueDate: "",
  assignedMemberId: "",
  organizationId: "",
};

function TasksPage() {
  const user = getCurrentUser();
  const isAdmin = user?.role === "ADMIN";

  const [tasks, setTasks] = useState([]);
  const [members, setMembers] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadTasks();
    loadMembers();
  }, []);

  async function loadTasks() {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/tasks");
      setTasks(response.data);
    } catch (err) {
      console.error("Error loading tasks:", err);
      setError("No se pudieron cargar las tareas.");
    } finally {
      setLoading(false);
    }
  }

  async function loadMembers() {
    try {
      const response = await api.get("/members");
      setMembers(response.data);
    } catch (err) {
      console.error("Error loading members:", err);
    }
  }

  function openCreateModal() {
    setEditingTask(null);

    setForm({
      ...emptyForm,
      organizationId: user?.organizationId || "",
    });

    setShowModal(true);
  }

  function openEditModal(task) {
    setEditingTask(task);

    setForm({
      title: task.title || "",
      description: task.description || "",
      status: task.status || "TODO",
      priority: task.priority || "MEDIUM",
      dueDate: task.dueDate || "",
      assignedMemberId: task.assignedMemberId
        ? String(task.assignedMemberId)
        : "",
      organizationId: task.organizationId || user?.organizationId || "",
    });

    setShowModal(true);
  }

  function closeModal() {
    if (saving) return;

    setShowModal(false);
    setEditingTask(null);
    setForm(emptyForm);
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!form.title.trim()) {
      setError("El título es obligatorio.");
      return;
    }

    try {
      setSaving(true);
      setError("");

      const payload = {
        title: form.title,
        description: form.description,
        status: form.status,
        priority: form.priority,
        dueDate: form.dueDate || null,
        assignedMemberId: form.assignedMemberId
          ? Number(form.assignedMemberId)
          : null,
        organizationId: Number(user?.organizationId || form.organizationId),
      };

      if (editingTask) {
        await api.put(`/tasks/${editingTask.id}`, payload);
      } else {
        await api.post("/tasks", payload);
      }

      closeModal();
      await loadTasks();
    } catch (err) {
      console.error("Error saving task:", err);

      const backendMessage =
        err.response?.data?.message ||
        err.response?.data?.error ||
        "No se pudo guardar la tarea.";

      setError(backendMessage);
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(task) {
    const confirmed = window.confirm(
      `¿Seguro que deseas eliminar la tarea "${task.title}"?`,
    );

    if (!confirmed) return;

    try {
      setError("");

      await api.delete(`/tasks/${task.id}`);
      await loadTasks();
    } catch (err) {
      console.error("Error deleting task:", err);

      const backendMessage =
        err.response?.data?.message ||
        err.response?.data?.error ||
        "No se pudo eliminar la tarea.";

      setError(backendMessage);
    }
  }

  function getMemberName(memberId) {
    if (!memberId) return "Sin asignar";

    const member = members.find((item) => item.id === memberId);

    if (!member) return "Miembro no encontrado";

    return `${member.firstName} ${member.lastName}`;
  }

  function getStatusLabel(status) {
    switch (status) {
      case "TODO":
        return "Por hacer";
      case "IN_PROGRESS":
        return "En progreso";
      case "DONE":
        return "Completada";
      default:
        return status;
    }
  }

  function getPriorityLabel(priority) {
    switch (priority) {
      case "LOW":
        return "Baja";
      case "MEDIUM":
        return "Media";
      case "HIGH":
        return "Alta";
      default:
        return priority;
    }
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h1>Tasks</h1>
          <p>Manage your team's tasks.</p>
        </div>

        {isAdmin && (
          <button className="primary-button" onClick={openCreateModal}>
            <Plus size={18} />
            New task
          </button>
        )}
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="empty-state">Loading tasks...</div>
      ) : tasks.length === 0 ? (
        <div className="empty-state">
          <h3>No tasks yet</h3>
          <p>
            {isAdmin
              ? "Create your first task to start organizing your team."
              : "There are no tasks in your organization."}
          </p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Title</th>
                <th>Status</th>
                <th>Priority</th>
                <th>Due date</th>
                <th>Assigned to</th>
                {isAdmin && <th>Actions</th>}
              </tr>
            </thead>

            <tbody>
              {tasks.map((task) => (
                <tr key={task.id}>
                  <td>
                    <strong>{task.title}</strong>

                    {task.description && (
                      <small className="table-description">
                        {task.description}
                      </small>
                    )}
                  </td>

                  <td>
                    <span
                      className={`status-badge status-${String(
                        task.status,
                      ).toLowerCase()}`}
                    >
                      {getStatusLabel(task.status)}
                    </span>
                  </td>

                  <td>
                    <span
                      className={`priority-badge priority-${String(
                        task.priority,
                      ).toLowerCase()}`}
                    >
                      {getPriorityLabel(task.priority)}
                    </span>
                  </td>

                  <td>{task.dueDate || "—"}</td>

                  <td>{getMemberName(task.assignedMemberId)}</td>

                  {isAdmin && (
                    <td>
                      <div className="table-actions">
                        <button
                          className="icon-button"
                          title="Edit task"
                          onClick={() => openEditModal(task)}
                        >
                          <Pencil size={17} />
                        </button>

                        <button
                          className="icon-button danger"
                          title="Delete task"
                          onClick={() => handleDelete(task)}
                        >
                          <Trash2 size={17} />
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
                <h2>{editingTask ? "Edit task" : "Create task"}</h2>

                <p>
                  {editingTask
                    ? "Update the task information."
                    : "Add a new task to your organization."}
                </p>
              </div>

              <button
                className="icon-button"
                onClick={closeModal}
                disabled={saving}
              >
                <X size={20} />
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
                  placeholder="Enter task title"
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
                  placeholder="Describe the task"
                  rows="4"
                />
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label htmlFor="status">Status</label>

                  <select
                    id="status"
                    name="status"
                    value={form.status}
                    onChange={handleChange}
                  >
                    <option value="TODO">Por hacer</option>

                    <option value="IN_PROGRESS">En progreso</option>

                    <option value="DONE">Completada</option>
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="priority">Priority</label>

                  <select
                    id="priority"
                    name="priority"
                    value={form.priority}
                    onChange={handleChange}
                  >
                    <option value="LOW">Baja</option>

                    <option value="MEDIUM">Media</option>

                    <option value="HIGH">Alta</option>
                  </select>
                </div>
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label htmlFor="dueDate">Due date</label>

                  <input
                    id="dueDate"
                    name="dueDate"
                    type="date"
                    value={form.dueDate}
                    onChange={handleChange}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="assignedMemberId">Assign to</label>

                  <select
                    id="assignedMemberId"
                    name="assignedMemberId"
                    value={form.assignedMemberId}
                    onChange={handleChange}
                  >
                    <option value="">Sin asignar</option>

                    {members.map((member) => (
                      <option key={member.id} value={member.id}>
                        {member.firstName} {member.lastName}
                      </option>
                    ))}
                  </select>
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
                    : editingTask
                      ? "Save changes"
                      : "Create task"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default TasksPage;
