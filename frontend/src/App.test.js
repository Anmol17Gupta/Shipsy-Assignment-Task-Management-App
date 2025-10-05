import { render, screen, fireEvent } from '@testing-library/react';
import App from './App';
import { BrowserRouter } from 'react-router-dom'; // if using router

// Mock API functions
jest.mock('./api', () => ({
  loginUser: jest.fn(),
  fetchTasks: jest.fn(() => Promise.resolve([])),
}));

describe('App Component', () => {
  test('renders login form initially', () => {
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );

    expect(screen.getByText(/Username/i)).toBeInTheDocument();
    expect(screen.getByText(/Password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Login/i })).toBeInTheDocument();
  });

  test('shows task form and list after login', () => {
    // Mock successful login
    require('./api').loginUser.mockResolvedValue({ login: 'success' });

    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );

    // Simulate login
    fireEvent.change(screen.getByPlaceholderText(/Username/i), {
      target: { value: 'demo' },
    });
    fireEvent.change(screen.getByPlaceholderText(/Password/i), {
      target: { value: 'demo123' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Login/i }));

    // After login, TaskForm and TaskList should be visible
    expect(screen.getByText(/Create Task/i)).toBeInTheDocument();
  });
});
