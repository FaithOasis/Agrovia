export default function Features() {
  return (
    <section id="features" className="section">
      <div className="container">
        <h2 className="title has-text-dark has-text-centered">Why Choose Agrovia?</h2>
        

        <div className="columns is-multiline has-text-centered">
          <div className="column is-one-third">
            <span className="icon is-large">
              <i className="fas fa-users"></i>
            </span>
            <h3 className="title has-text-dark is-5">Collab Share</h3>
            <p>Stay ahead with Resource Share Tailored farming.</p>
          </div>

          <div className="column is-one-third">
            <span className="icon is-large">
              <i className="fas fa-seedling"></i>
            </span>
            <h3 className="title has-text-dark is-5">Smart Harvest Hub</h3>
            <p>Monitor Crop Health using real insights and expert recommendations.</p>
          </div>

          <div className="column is-one-third">
            <span className="icon is-large">
              <i className="fas fa-store"></i>
            </span>
            <h3 className="title has-text-dark is-5">Marketplace Access</h3>
            <p>Connect directly with buyers and get the best prices for your harvest.</p>
          </div>
        </div>
      </div>
    </section>
  );
}
