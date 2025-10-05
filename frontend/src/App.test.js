import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from './App';

// Mock API functions
jest.mock('./api', () => ({
  loginUser: jest.fn(),
  fetchTasks: jest.fn(() => Promise.resolve([])),
  createTask: jest.fn(),
  updateTask: jest.fn(),
  deleteTask: jest.fn(),
}));

describe('App Component', () => {
  beforeEach(() => {
    sessionStorage.clear();
    jest.clearAllMocks();
  });

  test('renders login form initially', () => {
    render(<App />);
    expect(screen.getByText(/Username/i)).toBeInTheDocument();
    expect(screen.getByText(/Password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Login/i })).toBeInTheDocument();
  });

  test('shows task form and list after login', async () => {
    // Mock token returned on successful login
    require('./api').loginUser.mockResolvedValue({ token: 'mock-token' });

    render(<App />);

    // Simulate login
    fireEvent.change(screen.getByPlaceholderText(/Username/i), {
      target: { value: 'demo' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Password/i), {
      target: { value: 'demo123' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Login/i }));

    // Wait for async UI update (App fetches tasks after setting token)
    await waitFor(() => {
      expect(screen.getByText(/Create Task/i)).toBeInTheDocument();
    });
    // Optionally check for Task List item
    expect(screen.getByRole('button', { name: /Logout/i })).toBeInTheDocument();
  });
});
