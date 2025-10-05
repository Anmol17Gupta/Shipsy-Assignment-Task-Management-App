import { useEffect, useState } from "react";
import { fetchTasks, deleteTask } from "../api";

export default function TaskList({ onEdit }) {
  const [tasks, setTasks] = useState([]);
  const [page, setPage] = useState(1);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchTasks(page)
      .then(data => {
        if (Array.isArray(data)) {
          setTasks(data);
          setError("");
        } else {
          setTasks([]);
          setError(data.error || "Failed to fetch tasks.");
        }
      })
      .catch(() => {
        setTasks([]);
        setError("Failed to fetch tasks.");
      });
  }, [page]);

  function handleDelete(id) {
    deleteTask(id)
      .then(() => fetchTasks(page))
      .then(data => {
        if (Array.isArray(data)) {
          setTasks(data);
          setError("");
        } else {
          setTasks([]);
          setError(data.error || "Failed to fetch tasks.");
        }
      })
      .catch(() => {
        setTasks([]);
        setError("Failed to fetch tasks.");
      });
  }

  return (
    <div>
      <h1>Tasks</h1>
      {error && <div className="error">{error}</div>}
      <ul>
        {Array.isArray(tasks) && tasks.map(t => (
          <li key={t.id}>
            {t.title} ({t.priority}) | Completed: {t.completed ? "Yes" : "No"} | Total: {t.totalTime}
            <button onClick={() => onEdit(t)}>Edit</button>
            <button onClick={() => handleDelete(t.id)}>Delete</button>
          </li>
        ))}
      </ul>
      <button disabled={page === 1} onClick={() => setPage(page - 1)}>Prev</button>
      <button onClick={() => setPage(page + 1)}>Next</button>
    </div>
  );
}
