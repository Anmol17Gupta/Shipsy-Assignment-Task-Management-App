import React, { useState, useEffect } from "react";
import {
  loginUser,
  fetchTasks,
  createTask,
  updateTask,
  deleteTask,
} from "./api";
import "./App.css";

// --- Components ---
const Login = ({ onLogin, error }) => {
  const [username, setUsername] = useState("demo");
  const [password, setPassword] = useState("demo123");

  const handleSubmit = (e) => {
    e.preventDefault();
    onLogin(username, password);
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit}>
        <h2>Login</h2>
        {error && <p className="error">{error}</p>}
        <input type="text" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} required />
        <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} required />
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

const TaskForm = ({ onAddTask }) => {
  const [title, setTitle] = useState("");
  const [priority, setPriority] = useState("Medium");
  const [estimatedTime, setEstimatedTime] = useState(1);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title) return;
    onAddTask({ title, priority, estimatedTime, completed: false });
    setTitle("");
    setPriority("Medium");
    setEstimatedTime(1);
  };

  return (
    <form onSubmit={handleSubmit} className="task-form">
      <input type="text" placeholder="New task title..." value={title} onChange={e => setTitle(e.target.value)} />
      <select value={priority} onChange={e => setPriority(e.target.value)}>
        <option value="High">High</option>
        <option value="Medium">Medium</option>
        <option value="Low">Low</option>
      </select>
      <input type="number" min="0" value={estimatedTime} onChange={e => setEstimatedTime(parseFloat(e.target.value))} />
      <button type="submit">Add Task</button>
    </form>
  );
};

// Only map if tasks as an array. Otherwise, show error.
const TaskList = ({ tasks, onUpdateTask, onDeleteTask }) => (
  <ul className="task-list">
    {Array.isArray(tasks)
      ? tasks.map((task) => (
          <li key={task.id} className={`task-item${task.completed ? " completed" : ""}`}>
            <div className="task-details">
              <span className="task-title">{task.title}</span>
              <span className={`task-priority ${task.priority?.toLowerCase()}`}>{task.priority}</span>
            </div>
            <div className="task-actions">
              <button onClick={() => onUpdateTask(task.id, { completed: !task.completed })}>
                {task.completed ? "Undo Complete" : "Complete"}
              </button>
              <button className="delete" onClick={() => onDeleteTask(task.id)}>Delete</button>
            </div>
          </li>
        ))
      : <li className="error">No tasks to display or unauthorized.</li>
    }
  </ul>
);

// --- Main App ---
function App() {
  const [tasks, setTasks] = useState([]);
  const [token, setToken] = useState(sessionStorage.getItem("token"));
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      (async () => {
        try {
          setLoading(true);
          const data = await fetchTasks();
          if (Array.isArray(data)) {
            setTasks(data);
            setError("");
          } else {
            setTasks([]);
            setError(data?.error || "Failed to fetch tasks.");
          }
        } catch (err) {
          setTasks([]);
          setError("Failed to fetch tasks. Please try again.");
        } finally {
          setLoading(false);
        }
      })();
    } else {
      setLoading(false);
    }
  }, [token]);

  const handleLogin = async (username, password) => {
    try {
      setError("");
      const response = await loginUser(username, password);
      if (response.token) {
        setToken(response.token);
        sessionStorage.setItem("token", response.token);
      } else {
        setError("Login failed: No token received.");
      }
    } catch (err) {
      setError("Invalid username or password.");
    }
  };

  const handleLogout = () => {
    setToken(null);
    sessionStorage.removeItem("token");
    setTasks([]);
  };

  const handleAddTask = async (taskData) => {
    try {
      const response = await createTask(taskData);
      setTasks(tasks.concat(response));
    } catch (err) {
      setError("Failed to add task.");
    }
  };

  const handleUpdateTask = async (id, updateData) => {
    try {
      const response = await updateTask(id, updateData);
      setTasks(tasks.map((task) => (task.id === id ? response : task)));
    } catch (err) {
      setError("Failed to update task.");
    }
  };

  const handleDeleteTask = async (id) => {
    try {
      await deleteTask(id);
      setTasks(tasks.filter((task) => task.id !== id));
    } catch (err) {
      setError("Failed to delete task.");
    }
  };

  if (!token) {
    return <Login onLogin={handleLogin} error={error} />;
  }

  return (
    <div className="app-container">
      <header>
        <h1>Task Manager</h1>
        <button onClick={handleLogout}>Logout</button>
      </header>
      <main>
        <TaskForm onAddTask={handleAddTask} />
        {error && <p className="error">{error}</p>}
        {loading
          ? <p>Loading tasks...</p>
          : <TaskList tasks={tasks} onUpdateTask={handleUpdateTask} onDeleteTask={handleDeleteTask} />}
      </main>
    </div>
  );
}

export default App;
