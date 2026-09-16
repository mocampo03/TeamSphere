import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import "./App.css";

import { ThemeProvider } from "./context/ThemeContext";

import DashboardLayout from "./layouts/DashboardLayout";
import ProtectedRoute from "./components/ProtectedRoute";

import DashboardPage from "./pages/DashboardPage";
import MembersPage from "./pages/MembersPage";
import TasksPage from "./pages/TasksPage";
import EventsPage from "./pages/EventsPage";
import ReportsPage from "./pages/ReportsPage";
import LoginPage from "./pages/LoginPage";
import AIAssistantPage from "./pages/AIAssistantPage";

function App() {
  return (
    <ThemeProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<DashboardLayout />}>
              <Route index element={<Navigate to="/dashboard" replace />} />

              <Route path="dashboard" element={<DashboardPage />} />

              <Route path="members" element={<MembersPage />} />

              <Route path="tasks" element={<TasksPage />} />

              <Route path="events" element={<EventsPage />} />

              <Route path="reports" element={<ReportsPage />} />

              <Route path="/ai-assistant" element={<AIAssistantPage />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
