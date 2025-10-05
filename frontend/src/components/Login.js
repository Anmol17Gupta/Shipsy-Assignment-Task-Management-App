import { useState } from "react";
import { loginUser } from "../api";

export default function Login({ onLogin }) {
  const [user, setUser] = useState("");
  const [pass, setPass] = useState("");
  const [error, setError] = useState("");

  async function handleLogin(e) {
    e.preventDefault();
    const res = await loginUser(user, pass);
    if (res.login === "success") {
      onLogin();
    } else {
      setError("Invalid credentials");
    }
  }

  return (
    <form onSubmit={handleLogin}>
      <input value={user} onChange={e => setUser(e.target.value)} placeholder="Username"/>
      <input type="password" value={pass} onChange={e => setPass(e.target.value)} placeholder="Password"/>
      <button type="submit">Login</button>
      {error && <div>{error}</div>}
    </form>
  );
}
