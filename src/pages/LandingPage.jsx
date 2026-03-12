import Navbar from "../components/Navbar";
import Hero from "../components/Hero";
import Features from "../components/Features";
import Footer from "../components/Footer";

export default function LandingPage() {
  return (
    <div className="has-background-light is-flex is-flex-direction-column" style={{ minHeight: "100vh" }}>
      <Navbar />
      <main className="is-flex-grow-1">
        <Hero />
        <Features />
      </main>
      <Footer />
    </div>
  );
}
