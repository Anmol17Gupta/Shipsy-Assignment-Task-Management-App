import { useState } from "react";
import { createTask, updateTask } from "../api";

export default function TaskForm({ existing, onDone }) {
  const [form, setForm] = useState(existing || {
    title: "",
    priority: "Low",
    completed: false,
    estimated_time: "",
    actual_time: ""
  });

  function handleChange(e) {
    const { name, value, type, checked } = e.target;
    setForm(f => ({ ...f, [name]: type === "checkbox" ? checked : value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (existing) {
      await updateTask(existing.id, form);
    } else {
      await createTask(form);
    }
    onDone();
  }

  return (
    <form onSubmit={handleSubmit}>
      <input name="title" value={form.title} onChange={handleChange} placeholder="Title" required />
      <select name="priority" value={form.priority} onChange={handleChange}>
        <option>Low</option>
        <option>Medium</option>
        <option>High</option>
      </select>
      <label>
        Completed <input type="checkbox" name="completed" checked={form.completed} onChange={handleChange} />
      </label>
      <input name="estimated_time" type="number" value={form.estimated_time} onChange={handleChange} placeholder="Estimated" required />
      <input name="actual_time" type="number" value={form.actual_time} onChange={handleChange} placeholder="Actual" required />
      <button type="submit">{existing ? "Update" : "Create"} Task</button>
    </form>
  );
}
