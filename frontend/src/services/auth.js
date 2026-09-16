export function getCurrentUser() {
  const token = localStorage.getItem("token");

  if (!token) return null;

  try {
    const payload = token.split(".")[1];

    const decodedPayload = JSON.parse(
      atob(payload.replace(/-/g, "+").replace(/_/g, "/")),
    );

    const name =
      decodedPayload.name ||
      decodedPayload.fullName ||
      decodedPayload.username ||
      decodedPayload.email ||
      decodedPayload.sub ||
      "User";

    let role =
      decodedPayload.role ||
      decodedPayload.roles ||
      decodedPayload.authorities ||
      "USER";

    if (Array.isArray(role)) {
      role = role[0];
    }

    role = String(role).replace("ROLE_", "").replace("_", " ");

    return {
      name,
      role,
      email: decodedPayload.email || decodedPayload.sub,
      organizationId: decodedPayload.organizationId
        ? Number(decodedPayload.organizationId)
        : null,
    };
  } catch (error) {
    console.error("Error reading authentication token:", error);
    return null;
  }
}
