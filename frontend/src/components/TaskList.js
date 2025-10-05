import { useEffect, useState } from "react";
import { fetchTasks, deleteTask } from "../api";

export default function TaskList({ onEdit }) {
  const [tasks, setTasks] = useState([]);
  const [page, setPage] = useState(1);

  useEffect(() => {
    fetchTasks(page).then(setTasks);
  }, [page]);

  function handleDelete(id) {
    deleteTask(id).then(() => fetchTasks(page).then(setTasks));
  }

  return (
    <div>
      <h1>Tasks</h1>
      <ul>
        {tasks.map(t => (
          <li key={t.id}>
            {t.title} ({t.priority}) | Completed: {t.completed ? "Yes" : "No"} | Total: {t.total_time}
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
