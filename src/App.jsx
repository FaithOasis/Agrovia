import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/login/LoginPage";
import SignupPage from "./pages/signup/SignupPage";
import Dashboard from "./pages/dashboard/Dashboard";
import Home from "./pages/home/home";
import Community from "./pages/community/Community";

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />

          {/* Dashboard Layout with nested pages */}
          <Route path="/dashboard" element={<Dashboard />}>
            <Route index element={<Home />} />
            <Route path="community" element={<Community />} />
            {/* Future nested routes go here */}
          </Route>

          {/* Redirect old community path to dashboard version */}
          <Route path="/community" element={<Navigate to="/dashboard/community" replace />} />
        </Routes>
      </Router>
    </QueryClientProvider>
  );
}

export default App;