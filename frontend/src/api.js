const baseUrl = "http://localhost:8080";

export async function loginUser(username, password) {
  const res = await fetch(`${baseUrl}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  return res.json();
}

export async function fetchTasks(page = 1, priority = "") {
  const url = `${baseUrl}/tasks?page=${page}${priority ? `&priority=${priority}` : ""}`;
  const res = await fetch(url, { credentials: "include" });
  return res.json();
}

export async function createTask(task) {
  const res = await fetch(`${baseUrl}/tasks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(task),
  });
  return res.json();
}

export async function updateTask(id, updates) {
  const res = await fetch(`${baseUrl}/tasks/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(updates),
  });
  return res.json();
}

export async function deleteTask(id) {
  const res = await fetch(`${baseUrl}/tasks/${id}`, {
    method: "DELETE",
    credentials: "include",
  });
  return res.json();
}
