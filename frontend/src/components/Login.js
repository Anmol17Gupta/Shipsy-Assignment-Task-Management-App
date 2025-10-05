import { useState } from "react";
import { loginUser } from "../api";

export default function Login({ onLogin }) {
  const [user, setUser] = useState("");
  const [pass, setPass] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleLogin(e) {
    e.preventDefault();
    setError("");  // Clear previous error
    if (!user || !pass) {
      setError("Username and password are required.");
      return;
    }
    setLoading(true);
    try {
      const res = await loginUser(user, pass);
      if (res.login === "success") {
        onLogin();
      } else {
        setError("Invalid username or password.");
      }
    } catch (err) {
      setError("An error occurred while connecting to server.");
      console.error("Login error:", err);
    }
    setLoading(false);
  }

  return (
    <form onSubmit={handleLogin}>
      <label>
        Username
        <input 
          value={user} 
          onChange={e => setUser(e.target.value)} 
          placeholder="Username" 
          aria-label="username" 
          required 
        />
      </label>
      <label>
        Password
        <input 
          type="password" 
          value={pass} 
          onChange={e => setPass(e.target.value)} 
          placeholder="Password" 
          aria-label="password" 
          required 
        />
      </label>
      <button type="submit" disabled={loading}>
        {loading ? "Logging in..." : "Login"}
      </button>
      {error && <div style={{ color: "red", marginTop: "8px" }}>{error}</div>}
    </form>
  );
}
