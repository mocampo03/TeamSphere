import { createContext, useContext, useEffect, useState } from "react";

const LanguageContext = createContext();

const translations = {
  es: {
    sidebar: {
      dashboard: "Dashboard",
      members: "Miembros",
      tasks: "Tareas",
      events: "Eventos",
      reports: "Reportes",
      aiAssistant: "Asistente IA",
      workspace: "Espacio de trabajo",
      settings: "Configuración",
      appearance: "Apariencia",
      darkMode: "Modo oscuro",
      lightMode: "Modo claro",
      customizeWorkspace: "Personalizá tu espacio de trabajo",
      switchAppearance: "Cambiar apariencia",
      language: "Idioma",
      spanish: "Español",
      english: "Inglés",
      logout: "Cerrar sesión",
    },

    topbar: {
      search: "Buscar...",
      notifications: "Notificaciones",
      profile: "Perfil",
    },

    common: {
      save: "Guardar",
      cancel: "Cancelar",
      delete: "Eliminar",
      edit: "Editar",
      create: "Crear",
      close: "Cerrar",
      loading: "Cargando...",
    },
  },

  en: {
    sidebar: {
      dashboard: "Dashboard",
      members: "Members",
      tasks: "Tasks",
      events: "Events",
      reports: "Reports",
      aiAssistant: "AI Assistant",
      workspace: "Workspace",
      settings: "Settings",
      appearance: "Appearance",
      darkMode: "Dark mode",
      lightMode: "Light mode",
      customizeWorkspace: "Customize your workspace",
      switchAppearance: "Switch workspace appearance",
      language: "Language",
      spanish: "Spanish",
      english: "English",
      logout: "Logout",
    },

    topbar: {
      search: "Search...",
      notifications: "Notifications",
      profile: "Profile",
    },

    common: {
      save: "Save",
      cancel: "Cancel",
      delete: "Delete",
      edit: "Edit",
      create: "Create",
      close: "Close",
      loading: "Loading...",
    },
  },
};

export function LanguageProvider({ children }) {
  const [language, setLanguage] = useState(() => {
    return localStorage.getItem("teamsphere-language") || "es";
  });

  useEffect(() => {
    localStorage.setItem("teamsphere-language", language);
  }, [language]);

  const changeLanguage = (newLanguage) => {
    if (translations[newLanguage]) {
      setLanguage(newLanguage);
    }
  };

  const t = (key) => {
    const keys = key.split(".");

    let value = translations[language];

    for (const currentKey of keys) {
      value = value?.[currentKey];
    }

    return value || key;
  };

  return (
    <LanguageContext.Provider
      value={{
        language,
        changeLanguage,
        t,
      }}
    >
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  return useContext(LanguageContext);
}
