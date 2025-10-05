import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import './App.css';

// --- Configuration ---
const API_URL = 'http://127.0.0.1:5000';

// --- API Client ---
const apiClient = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// --- Components ---

const Login = ({ onLogin, error }) => {
    const [username, setUsername] = useState('demo');
    const [password, setPassword] = useState('demo123');

    const handleSubmit = (e) => {
        e.preventDefault();
        onLogin(username, password);
    };

    return (
        <div className="login-container">
            <form onSubmit={handleSubmit}>
                <h2>Login</h2>
                {error && <p className="error">{error}</p>}
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

const TaskForm = ({ onAddTask }) => {
    const [title, setTitle] = useState('');
    const [priority, setPriority] = useState('Medium');
    const [estimatedTime, setEstimatedTime] = useState(1);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!title) return;
        onAddTask({ title, priority, estimated_time: estimatedTime, completed: false });
        setTitle('');
        setPriority('Medium');
        setEstimatedTime(1);
    };

    return (
        <form onSubmit={handleSubmit} className="task-form">
            <input
                type="text"
                placeholder="New task title..."
                value={title}
                onChange={(e) => setTitle(e.target.value)}
            />
            <select value={priority} onChange={(e) => setPriority(e.target.value)}>
                <option value="High">High</option>
                <option value="Medium">Medium</option>
                <option value="Low">Low</option>
            </select>
            <input
                type="number"
                min="0"
                value={estimatedTime}
                onChange={(e) => setEstimatedTime(parseFloat(e.target.value))}
            />
            <button type="submit">Add Task</button>
        </form>
    );
};

const TaskList = ({ tasks, onUpdateTask, onDeleteTask }) => (
    <ul className="task-list">
        {tasks.map((task) => (
            <li key={task.id} className={`task-item ${task.completed ? 'completed' : ''}`}>
                <div className="task-details">
                    <span className="task-title">{task.title}</span>
                    <span className={`task-priority ${task.priority?.toLowerCase()}`}>{task.priority}</span>
                </div>
                <div className="task-actions">
                    <button onClick={() => onUpdateTask(task.id, { completed: !task.completed })}>
                        {task.completed ? 'Undo' : 'Complete'}
                    </button>
                    <button className="delete" onClick={() => onDeleteTask(task.id)}>Delete</button>
                </div>
            </li>
        ))}
    </ul>
);


// --- Main App Component ---

function App() {
    const [tasks, setTasks] = useState([]);
    const [token, setToken] = useState(sessionStorage.getItem('token'));
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    // This effect handles setting the auth header whenever the token changes.
    useEffect(() => {
        apiClient.defaults.headers.common['Authorization'] = token ? `Bearer ${token}` : '';
    }, [token]);

    const fetchTasks = useCallback(async () => {
        try {
            setLoading(true);
            const response = await apiClient.get('/tasks');
            setTasks(response.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch tasks. Please try again.');
            console.error(err);
            // If the token is invalid (e.g., expired), log the user out.
            if (err.response && err.response.status === 401) {
                handleLogout();
            }
        } finally {
            setLoading(false);
        }
    }, []); // Empty dependency array is correct here.

    // This effect handles fetching tasks when the component mounts or when the user logs in.
    useEffect(() => {
        if (token) {
            fetchTasks();
        } else {
            // If there's no token, we're not loading anything.
            setLoading(false);
        }
    }, [token, fetchTasks]);

    const handleLogin = async (username, password) => {
        try {
            const response = await apiClient.post('/login', { username, password });
            if (response.data.token) {
                const receivedToken = response.data.token;
                setToken(receivedToken);
                sessionStorage.setItem('token', receivedToken);
                setError('');
            } else {
                setError('Login failed: No token received.');
            }
        } catch (err) {
            setError('Invalid username or password.');
            console.error(err);
        }
    };

    const handleLogout = () => {
        setToken(null);
        sessionStorage.removeItem('token');
        setTasks([]);
    };

    const handleAddTask = async (taskData) => {
        try {
            const response = await apiClient.post('/tasks', taskData);
            setTasks([...tasks, response.data]);
        } catch (err) {
            setError('Failed to add task.');
            console.error(err);
        }
    };

    const handleUpdateTask = async (id, updateData) => {
        try {
            const response = await apiClient.put(`/tasks/${id}`, updateData);
            setTasks(tasks.map((task) => (task.id === id ? response.data : task)));
        } catch (err) {
            setError('Failed to update task.');
            console.error(err);
        }
    };

    const handleDeleteTask = async (id) => {
        try {
            await apiClient.delete(`/tasks/${id}`);
            setTasks(tasks.filter((task) => task.id !== id));
        } catch (err) {
            setError('Failed to delete task.');
            console.error(err);
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
                {loading ? (
                    <p>Loading tasks...</p>
                ) : (
                    <TaskList tasks={tasks} onUpdateTask={handleUpdateTask} onDeleteTask={handleDeleteTask} />
                )}
            </main>
        </div>
    );
}

export default App;