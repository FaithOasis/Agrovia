import { useState } from "react";

export default function AuthForm({
  fields,
  buttonText,
  onSubmit
}) {

  const initialState = {};
  fields.forEach(field => initialState[field.name] = "");

  const [formData, setFormData] = useState(initialState);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    for (let key in formData) {
      if (!formData[key] && key !== "farm") {
        setError("Please fill all required fields");
        return;
      }
    }

    try {
      setLoading(true);
      await onSubmit(formData);
      setLoading(false);
    } catch (err) {
      setError("Something went wrong");
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>

      {error && (
        <div className="notification is-danger is-light">
          {error}
        </div>
      )}

      {fields.map((field) => (
        <div className="field" key={field.name}>
          <label className="label has-text-white">
            {field.label}
          </label>

          <div className={`control ${field.type === "password" ? "has-icons-right" : ""}`}>

            <input
              type={
                field.type === "password"
                  ? (showPassword ? "text" : "password")
                  : field.type
              }
              name={field.name}
              value={formData[field.name]}
              onChange={handleChange}
              className="input is-rounded"
              placeholder={field.placeholder}
            />

            {field.type === "password" && (
              <span
                className="icon is-small is-right"
                style={{ cursor: "pointer" }}
                onClick={() => setShowPassword(!showPassword)}
              >
                👁️
              </span>
            )}

          </div>
        </div>
      ))}

      <div className="field mt-4">
        <div className="control">
          <button
            type="submit"
            className={`button is-success is-fullwidth is-rounded ${
              loading ? "is-loading" : ""
            }`}
          >
            {buttonText}
          </button>
        </div>
      </div>

    </form>
  );
}