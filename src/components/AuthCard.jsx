import "./authCard.css";

export default function AuthCard({ title, children }) {
  return (
    <div className="auth-card">

      <div className="has-text-centered mb-5">
        <img
          src="/logo-dark-transparent.png"
          alt="Agrovia"
          className="auth-logo"
        />

        <h1 className="title has-text-white">{title}</h1>
      </div>

      {children}

    </div>
  );
}