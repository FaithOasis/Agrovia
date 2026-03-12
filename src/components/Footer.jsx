export default function Footer() {
  return (
    <footer className="footer has-background-light">
      <div className="content has-text-centered">
        <p>
          © {new Date().getFullYear()} <strong>Agrovia</strong>. All rights reserved.
        </p>
        <p>
          <a href="#privacy">Privacy Policy</a> | <a href="#terms">Terms of Service</a>
        </p>
      </div>
    </footer>
  );
}
