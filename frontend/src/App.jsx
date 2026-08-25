import { useState } from "react";
import "./App.css";

const API_URL = "http://localhost:8080/api/users";

function App() {
  const [activeTab, setActiveTab] = useState("register");

  const [registerData, setRegisterData] = useState({
    username: "",
    password: "",
    sensitiveData: "",
    capabilityCode: "",
  });

  const [userId, setUserId] = useState("");
  const [user, setUser] = useState(null);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    setRegisterData({
      ...registerData,
      [event.target.name]: event.target.value,
    });
  };

  const registerUser = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");
    setLoading(true);

    try {
      const response = await fetch(`${API_URL}/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(registerData),
      });

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        throw new Error(
          typeof data === "string"
            ? data
            : data?.message || "Registration failed"
        );
      }

      setMessage(
        `User registered successfully. User ID: ${data.id}`
      );

      setRegisterData({
        username: "",
        password: "",
        sensitiveData: "",
        capabilityCode: "",
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const retrieveUser = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");
    setUser(null);

    if (!userId) {
      setError("Please enter a User ID.");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(`${API_URL}/${userId}`);

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        throw new Error(
          typeof data === "string"
            ? data
            : data?.message || "User not found"
        );
      }

      setUser(data);
      setMessage("User retrieved successfully.");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">
      <header className="navbar">
        <div className="brand">
          <div className="brand-icon">🔐</div>

          <div>
            <h1>SecureCloud</h1>
            <span>Secure Data Management</span>
          </div>
        </div>

        <div className="status">
          <span className="status-dot"></span>
          System Online
        </div>
      </header>

      <main className="container">
        <section className="hero">
          <div>
            <p className="eyebrow">SECURE CLOUD PLATFORM</p>

            <h2>
              Protect your data.
              <br />
              <span>Control your security.</span>
            </h2>

            <p className="hero-text">
              SecureCloud protects user passwords with BCrypt
              hashing and sensitive information with AES encryption.
            </p>
          </div>

          <div className="security-card">
            <div className="security-icon">🛡️</div>
            <h3>Enterprise Security</h3>
            <p>
              Your sensitive information is protected with
              modern encryption technology.
            </p>

            <div className="security-items">
              <span>✓ BCrypt Password Hashing</span>
              <span>✓ AES Data Encryption</span>
              <span>✓ Capability Validation</span>
            </div>
          </div>
        </section>

        <section className="workspace">
          <div className="tabs">
            <button
              className={
                activeTab === "register"
                  ? "tab active"
                  : "tab"
              }
              onClick={() => {
                setActiveTab("register");
                setMessage("");
                setError("");
              }}
            >
              Register User
            </button>

            <button
              className={
                activeTab === "retrieve"
                  ? "tab active"
                  : "tab"
              }
              onClick={() => {
                setActiveTab("retrieve");
                setMessage("");
                setError("");
              }}
            >
              Retrieve User
            </button>
          </div>

          {activeTab === "register" && (
            <form
              className="form-card"
              onSubmit={registerUser}
            >
              <div className="form-heading">
                <h3>Create Secure User</h3>
                <p>
                  Enter the details below to create a protected
                  SecureCloud account.
                </p>
              </div>

              <div className="form-grid">
                <div className="field">
                  <label>Username</label>
                  <input
                    type="text"
                    name="username"
                    value={registerData.username}
                    onChange={handleChange}
                    placeholder="Enter username"
                    required
                  />
                </div>

                <div className="field">
                  <label>Password</label>
                  <input
                    type="password"
                    name="password"
                    value={registerData.password}
                    onChange={handleChange}
                    placeholder="Minimum 6 characters"
                    required
                  />
                </div>

                <div className="field full">
                  <label>Sensitive Data</label>
                  <textarea
                    name="sensitiveData"
                    value={registerData.sensitiveData}
                    onChange={handleChange}
                    placeholder="Enter sensitive information"
                    rows="4"
                    required
                  />
                </div>

                <div className="field full">
                  <label>Capability Code</label>
                  <input
                    type="text"
                    name="capabilityCode"
                    value={registerData.capabilityCode}
                    onChange={handleChange}
                    placeholder="Enter capability code"
                    required
                  />
                  <small>
                    Current development code: CAP001
                  </small>
                </div>
              </div>

              <button
                className="primary-button"
                type="submit"
                disabled={loading}
              >
                {loading
                  ? "Registering..."
                  : "Register Securely"}
              </button>
            </form>
          )}

          {activeTab === "retrieve" && (
            <form
              className="form-card"
              onSubmit={retrieveUser}
            >
              <div className="form-heading">
                <h3>Retrieve User</h3>
                <p>
                  Enter a User ID to securely retrieve the
                  stored information.
                </p>
              </div>

              <div className="field">
                <label>User ID</label>

                <input
                  type="number"
                  value={userId}
                  onChange={(event) =>
                    setUserId(event.target.value)
                  }
                  placeholder="Example: 8"
                  min="1"
                  required
                />
              </div>

              <button
                className="primary-button"
                type="submit"
                disabled={loading}
              >
                {loading
                  ? "Retrieving..."
                  : "Retrieve User"}
              </button>

              {user && (
                <div className="user-result">
                  <div className="result-header">
                    <span>✓</span>
                    <h4>User Retrieved</h4>
                  </div>

                  <div className="result-row">
                    <span>User ID</span>
                    <strong>{user.id}</strong>
                  </div>

                  <div className="result-row">
                    <span>Username</span>
                    <strong>{user.username}</strong>
                  </div>

                  <div className="result-row">
                    <span>Sensitive Data</span>
                    <strong>{user.sensitiveData}</strong>
                  </div>
                </div>
              )}
            </form>
          )}

          {message && (
            <div className="success-message">
              ✓ {message}
            </div>
          )}

          {error && (
            <div className="error-message">
              ✕ {error}
            </div>
          )}
        </section>
      </main>

      <footer>
        <p>
          SecureCloud © 2026 • Secure Data Management Platform
        </p>
      </footer>
    </div>
  );
}

export default App;