import { useNavigate, Link } from "react-router-dom";
import AuthCard from "../../components/AuthCard";
import AuthForm from "../../components/AuthForm";
import "./login.css";

export default function LoginPage() {

  const navigate = useNavigate();

  const fields = [
    {
      name: "email",
      label: "Email",
      type: "email",
      placeholder: "Enter your email"
    },
    {
      name: "password",
      label: "Password",
      type: "password",
      placeholder: "Enter your password"
    }
  ];

  const handleLogin = async (data) => {
    console.log("Login:", data);

    setTimeout(() => {
      navigate("/dashboard");
    }, 1000);
  };

  return (

    <div className="login-page">

      <AuthCard title="Login to Agrovia">

        <AuthForm
          fields={fields}
          buttonText="Login"
          onSubmit={handleLogin}
        />

        <p className="has-text-grey-light is-size-7 has-text-centered mt-4">
          Don't have an account?{" "}
          <Link to="/signup" className="has-text-success">
            Sign up
          </Link>
        </p>

      </AuthCard>

    </div>

  );
}