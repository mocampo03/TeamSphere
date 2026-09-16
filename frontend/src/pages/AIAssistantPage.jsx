import { useState } from "react";
import { Send, Bot, User, Loader2 } from "lucide-react";
import api from "../services/api";

function AIAssistantPage() {
  const [messages, setMessages] = useState([
    {
      role: "assistant",
      content:
        "Hola. Soy el asistente de TeamSphere. Podés preguntarme sobre tus tareas, eventos, miembros y reportes.",
    },
  ]);

  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const message = input.trim();

    if (!message || loading) return;

    setMessages((previousMessages) => [
      ...previousMessages,
      {
        role: "user",
        content: message,
      },
    ]);

    setInput("");
    setLoading(true);

    try {
      const response = await api.post("/ai/chat", {
        message,
      });

      setMessages((previousMessages) => [
        ...previousMessages,
        {
          role: "assistant",
          content: response.data.response,
        },
      ]);
    } catch (error) {
      console.error("Error communicating with AI Assistant:", error);

      setMessages((previousMessages) => [
        ...previousMessages,
        {
          role: "assistant",
          content:
            "Ocurrió un error al comunicarse con el asistente. Intentá nuevamente.",
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="ai-assistant-page">
      <div className="page-header">
        <div>
          <h1>AI Assistant</h1>
          <p>
            Consultá información de tu organización usando lenguaje natural.
          </p>
        </div>
      </div>

      <div className="ai-chat-container">
        <div className="ai-chat-messages">
          {messages.map((message, index) => (
            <div
              key={index}
              className={`ai-message ${
                message.role === "user"
                  ? "ai-message-user"
                  : "ai-message-assistant"
              }`}
            >
              <div className="ai-message-icon">
                {message.role === "user" ? (
                  <User size={18} />
                ) : (
                  <Bot size={18} />
                )}
              </div>

              <div className="ai-message-content">
                <span className="ai-message-author">
                  {message.role === "user" ? "You" : "TeamSphere AI"}
                </span>

                <p>{message.content}</p>
              </div>
            </div>
          ))}

          {loading && (
            <div className="ai-message ai-message-assistant">
              <div className="ai-message-icon">
                <Bot size={18} />
              </div>

              <div className="ai-message-content">
                <span className="ai-message-author">TeamSphere AI</span>

                <div className="ai-loading">
                  <Loader2 size={18} className="ai-spinner" />
                  <span>Analizando información...</span>
                </div>
              </div>
            </div>
          )}
        </div>

        <form className="ai-chat-input-container" onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Preguntá sobre tus tareas, eventos o miembros..."
            value={input}
            onChange={(event) => setInput(event.target.value)}
            disabled={loading}
          />

          <button
            type="submit"
            disabled={!input.trim() || loading}
            aria-label="Send message"
          >
            {loading ? <Loader2 size={20} /> : <Send size={20} />}
          </button>
        </form>
      </div>
    </div>
  );
}

export default AIAssistantPage;
