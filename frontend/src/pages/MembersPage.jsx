import { useEffect, useMemo, useState } from "react";
import {
  Edit3,
  Mail,
  Phone,
  Plus,
  Search,
  Trash2,
  UserPlus,
  X,
} from "lucide-react";
import api from "../services/api";
import { getCurrentUser } from "../services/auth";

function MembersPage() {
  const [members, setMembers] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [editingMember, setEditingMember] = useState(null);
  const [saving, setSaving] = useState(false);

  const user = getCurrentUser();
  const isAdmin = user?.role === "ADMIN";
  const organizationId = user?.organizationId;

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phone: "",
    position: "",
    active: true,
    organizationId: null,
  });

  useEffect(() => {
    loadMembers();
  }, []);

  async function loadMembers() {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/members");
      setMembers(response.data);
    } catch (err) {
      console.error("Error loading members:", err);
      setError("Unable to load members.");
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingMember(null);

    setForm({
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      phone: "",
      position: "",
      active: true,
      organizationId: organizationId || null,
    });

    setShowModal(true);
  }

  function openEditModal(member) {
    setEditingMember(member);

    setForm({
      firstName: member.firstName || "",
      lastName: member.lastName || "",
      email: member.email || "",
      password: "",
      phone: member.phone || "",
      position: member.position || "",
      active: member.active ?? true,
      organizationId: member.organizationId || organizationId || null,
    });

    setShowModal(true);
  }

  function closeModal() {
    if (saving) return;

    setShowModal(false);
    setEditingMember(null);
  }

  function handleChange(event) {
    const { name, value, type, checked } = event.target;

    setForm((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");

      if (!editingMember && !organizationId) {
        setError("Unable to determine the user's organization.");
        return;
      }

      const payload = {
        ...form,
        organizationId: Number(
          editingMember ? form.organizationId : organizationId,
        ),
      };

      if (!editingMember && !payload.password) {
        setError("Password is required when creating a member.");
        return;
      }

      if (editingMember && !payload.password) {
        delete payload.password;
      }

      if (editingMember) {
        const response = await api.put(`/members/${editingMember.id}`, payload);

        setMembers((current) =>
          current.map((member) =>
            member.id === editingMember.id ? response.data : member,
          ),
        );
      } else {
        const response = await api.post("/members", payload);

        setMembers((current) => [...current, response.data]);
      }

      closeModal();
    } catch (err) {
      console.error("Error saving member:", err);

      const message =
        err.response?.data?.message ||
        "Unable to save member. Please check the information.";

      setError(message);
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(member) {
    const confirmed = window.confirm(
      `Are you sure you want to delete ${member.firstName} ${member.lastName}?`,
    );

    if (!confirmed) return;

    try {
      setError("");

      await api.delete(`/members/${member.id}`);

      setMembers((current) => current.filter((item) => item.id !== member.id));
    } catch (err) {
      console.error("Error deleting member:", err);

      setError(err.response?.data?.message || "Unable to delete member.");
    }
  }

  const filteredMembers = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) return members;

    return members.filter((member) => {
      const fullName = `${member.firstName} ${member.lastName}`.toLowerCase();

      return (
        fullName.includes(query) ||
        member.email?.toLowerCase().includes(query) ||
        member.position?.toLowerCase().includes(query) ||
        member.phone?.toLowerCase().includes(query)
      );
    });
  }, [members, search]);

  function getInitials(member) {
    return `${member.firstName?.[0] || ""}${
      member.lastName?.[0] || ""
    }`.toUpperCase();
  }

  return (
    <div className="members-page">
      <div className="page-header">
        <div>
          <p className="page-eyebrow">Organization</p>

          <h1>Members</h1>

          <p>Manage the people in your organization.</p>
        </div>

        {isAdmin && (
          <button className="primary-button" onClick={openCreateModal}>
            <Plus size={18} />
            Add member
          </button>
        )}
      </div>

      <div className="members-toolbar">
        <div className="members-search">
          <Search size={18} />

          <input
            type="text"
            placeholder="Search members..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>

        <div className="members-count">
          {filteredMembers.length} member
          {filteredMembers.length !== 1 ? "s" : ""}
        </div>
      </div>

      {error && !showModal && <div className="page-error">{error}</div>}

      <div className="members-card">
        {loading ? (
          <div className="members-empty">
            <div className="loading-spinner" />

            <p>Loading members...</p>
          </div>
        ) : filteredMembers.length === 0 ? (
          <div className="members-empty">
            <UserPlus size={42} />

            <h3>No members found</h3>

            <p>
              {search
                ? "Try a different search."
                : "Your organization does not have any members yet."}
            </p>

            {isAdmin && !search && (
              <button className="primary-button" onClick={openCreateModal}>
                <Plus size={18} />
                Add first member
              </button>
            )}
          </div>
        ) : (
          <div className="members-table-wrapper">
            <table className="members-table">
              <thead>
                <tr>
                  <th>Member</th>
                  <th>Contact</th>
                  <th>Position</th>
                  <th>Status</th>

                  {isAdmin && <th>Actions</th>}
                </tr>
              </thead>

              <tbody>
                {filteredMembers.map((member) => (
                  <tr key={member.id}>
                    <td>
                      <div className="member-identity">
                        <div className="member-avatar">
                          {getInitials(member)}
                        </div>

                        <div>
                          <strong>
                            {member.firstName} {member.lastName}
                          </strong>

                          <span>ID #{member.id}</span>
                        </div>
                      </div>
                    </td>

                    <td>
                      <div className="member-contact">
                        <span>
                          <Mail size={15} />
                          {member.email}
                        </span>

                        {member.phone && (
                          <span>
                            <Phone size={15} />
                            {member.phone}
                          </span>
                        )}
                      </div>
                    </td>

                    <td>
                      <span className="member-position">
                        {member.position || "—"}
                      </span>
                    </td>

                    <td>
                      <span
                        className={`status-badge ${
                          member.active ? "active" : "inactive"
                        }`}
                      >
                        <span className="status-dot" />

                        {member.active ? "Active" : "Inactive"}
                      </span>
                    </td>

                    {isAdmin && (
                      <td>
                        <div className="member-actions">
                          <button
                            className="icon-button"
                            title="Edit member"
                            onClick={() => openEditModal(member)}
                          >
                            <Edit3 size={17} />
                          </button>

                          <button
                            className="icon-button danger"
                            title="Delete member"
                            onClick={() => handleDelete(member)}
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
      </div>

      {showModal && (
        <div className="modal-overlay" onMouseDown={closeModal}>
          <div
            className="member-modal"
            onMouseDown={(event) => event.stopPropagation()}
          >
            <div className="modal-header">
              <div>
                <p className="page-eyebrow">
                  {editingMember ? "Member details" : "New member"}
                </p>

                <h2>{editingMember ? "Edit member" : "Add a member"}</h2>
              </div>

              <button
                className="modal-close"
                onClick={closeModal}
                disabled={saving}
              >
                <X size={20} />
              </button>
            </div>

            {error && <div className="modal-error">{error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="form-group">
                  <label htmlFor="firstName">First name</label>

                  <input
                    id="firstName"
                    name="firstName"
                    value={form.firstName}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="lastName">Last name</label>

                  <input
                    id="lastName"
                    name="lastName"
                    value={form.lastName}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group full">
                  <label htmlFor="email">Email</label>

                  <input
                    id="email"
                    name="email"
                    type="email"
                    value={form.email}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="phone">Phone</label>

                  <input
                    id="phone"
                    name="phone"
                    value={form.phone}
                    onChange={handleChange}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="position">Position</label>

                  <input
                    id="position"
                    name="position"
                    value={form.position}
                    onChange={handleChange}
                  />
                </div>

                <div className="form-group full">
                  <label htmlFor="password">
                    {editingMember ? "New password (optional)" : "Password"}
                  </label>

                  <input
                    id="password"
                    name="password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}
                    required={!editingMember}
                  />
                </div>

                <div className="form-group checkbox-group full">
                  <label>
                    <input
                      type="checkbox"
                      name="active"
                      checked={form.active}
                      onChange={handleChange}
                    />

                    <span>Member is active</span>
                  </label>
                </div>
              </div>

              <div className="modal-footer">
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
                    : editingMember
                      ? "Save changes"
                      : "Create member"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default MembersPage;
