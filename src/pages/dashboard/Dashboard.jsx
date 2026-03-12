import { Outlet } from "react-router-dom";
import { useState } from "react";
import Navbar from "../../components/navbar/Navbar";
import Menu from "../../components/menu/Menu";
import Footer from "../../components/footer/Footer";
import "./dashboard.scss";

const Dashboard = () => {
  const [collapsed, setCollapsed] = useState(false);

  const toggleMenu = () => setCollapsed((prev) => !prev);

  return (
    <div className="main">
      <Navbar onToggleMenu={toggleMenu} collapsed={collapsed} />

      <div className="dashboardLayout">
        <aside className={collapsed ? "menuContainer collapsed" : "menuContainer"}>
          <Menu collapsed={collapsed} />
        </aside>

        <main className="contentContainer">
          <Outlet />
        </main>
      </div>

      <Footer />
    </div>
  );
};

export default Dashboard;