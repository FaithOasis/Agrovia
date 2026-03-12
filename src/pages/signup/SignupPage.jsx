import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthCard from "../../components/AuthCard";
import "./signup.css";

export default function SignupPage() {

  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    farmName: "",
    farmType: "",
    region: "",
    farmSize: ""
  });

  const [showPassword, setShowPassword] = useState(false);

  const handleChange = ({ target }) => {
    const { name, value } = target;

    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSignup = (e) => {
    e.preventDefault();

    console.log("Signup Data:", formData);

    setTimeout(() => {
      navigate("/dashboard");
    }, 1000);
  };

  return (
    <div className="signup-page">

      <AuthCard title="Create Agrovia Account">

        <form onSubmit={handleSignup} className="signup-form">

          <div className="signup-grid">

            {/* LEFT SIDE */}

            <div className="signup-section">

              <p className="section-title">Account</p>

              <div className="field">
                <label className="label has-text-white">Full Name</label>
                <div className="control">
                  <input
                    name="name"
                    type="text"
                    className="input is-rounded"
                    placeholder="Enter your full name"
                    value={formData.name}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <div className="field">
                <label className="label has-text-white">Email</label>
                <div className="control">
                  <input
                    name="email"
                    type="email"
                    className="input is-rounded"
                    placeholder="Enter your email"
                    value={formData.email}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <div className="field">
                <label className="label has-text-white">Password</label>

                <div className="control password-wrapper">

                  <input
                    name="password"
                    type={showPassword ? "text" : "password"}
                    className="input is-rounded"
                    placeholder="Enter your password"
                    value={formData.password}
                    onChange={handleChange}
                  />

                  <span
                    className="password-toggle"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? "🙈" : "👁️"}
                  </span>

                </div>
              </div>

            </div>

            {/* VERTICAL DIVIDER */}

            <div className="signup-divider"></div>

            {/* RIGHT SIDE */}

            <div className="signup-section">

              <p className="section-title">Farm Profile</p>

              <div className="field">
                <label className="label has-text-white">Farm Name</label>
                <div className="control">
                  <input
                    name="farmName"
                    type="text"
                    className="input is-rounded"
                    placeholder="Enter farm name"
                    value={formData.farmName}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <div className="field">
                <label className="label has-text-white">Farm Type</label>

                <div className="control">
                  <div className="select is-fullwidth is-rounded">

                    <select
                      name="farmType"
                      value={formData.farmType}
                      onChange={handleChange}
                    >
                      <option value="">Select farm type</option>
                      <option value="crop">Crop Farming</option>
                      <option value="livestock">Livestock</option>
                      <option value="mixed">Mixed Farming</option>
                      <option value="horticulture">Horticulture</option>
                    </select>

                  </div>
                </div>
              </div>

              <div className="field">
                <label className="label has-text-white">Region</label>

                <div className="control">
                  <div className="select is-fullwidth is-rounded">

                    <select
                      name="region"
                      value={formData.region}
                      onChange={handleChange}
                    >
                      <option value="">Select region</option>
                      <option>Mashonaland East</option>
                      <option>Mashonaland West</option>
                      <option>Manicaland</option>
                      <option>Midlands</option>
                      <option>Masvingo</option>
                      <option>Matabeleland North</option>
                      <option>Matabeleland South</option>
                    </select>

                  </div>
                </div>
              </div>

              <div className="field">
                <label className="label has-text-white">
                  Farm Size (hectares)
                </label>

                <div className="control">
                  <input
                    name="farmSize"
                    type="number"
                    className="input is-rounded"
                    placeholder="Farm size"
                    value={formData.farmSize}
                    onChange={handleChange}
                  />
                </div>
              </div>

            </div>

          </div>

          {/* SUBMIT BUTTON */}

          <div className="field mt-5">
            <div className="control">
              <button
                type="submit"
                className="button is-success is-fullwidth is-rounded signup-button"
              >
                Create Account
              </button>
            </div>
          </div>

        </form>

        <p className="has-text-grey-light is-size-7 has-text-centered mt-4">
          Already have an account?{" "}
          <Link to="/login" className="has-text-success">
            Log in
          </Link>
        </p>

      </AuthCard>

    </div>
  );
}