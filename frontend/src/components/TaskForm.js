import { useState } from "react";
import { createTask, updateTask } from "../api";

export default function TaskForm({ existing, onDone }) {
  const [form, setForm] = useState(existing || {
    title: "",
    priority: "Low",
    completed: false,
    estimatedTime: "",
    actualTime: ""
  });

  function handleChange(e) {
    const { name, value, type, checked } = e.target;
    setForm(f => ({ 
      ...f, 
      [name]: type === "checkbox" ? checked : value 
    }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    // convert number fields to correct type
    const payload = {
      ...form,
      estimatedTime: parseFloat(form.estimatedTime) || 0,
      actualTime: parseFloat(form.actualTime) || 0
    };
    if (existing) {
      await updateTask(existing.id, payload);
    } else {
      await createTask(payload);
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
      <input name="estimatedTime" type="number" value={form.estimatedTime} onChange={handleChange} placeholder="Estimated" required />
      <input name="actualTime" type="number" value={form.actualTime} onChange={handleChange} placeholder="Actual" required />
      <button type="submit">{existing ? "Update" : "Create"} Task</button>
    </form>
  );
}
