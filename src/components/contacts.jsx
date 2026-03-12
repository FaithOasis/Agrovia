import React from "react";

export default function Contact() {
  return (
    <section className="section has-background-light" id="contact">
      <div className="container">
        <h2 className="title has-text-centered">Contact Us</h2>
        <p className="subtitle has-text-centered">
          Have questions? Reach out to our team and we’ll get back to you.
        </p>

        <div className="columns is-multiline mt-5">
          {/* Contact Info */}
          <div className="column is-one-third has-text-centered">
            <span className="icon is-large has-text-primary">
              <i className="fas fa-envelope fa-2x"></i>
            </span>
            <h3 className="title is-5 mt-3">Email</h3>
            <p>support@agrovia.com</p>
          </div>

          <div className="column is-one-third has-text-centered">
            <span className="icon is-large has-text-primary">
              <i className="fas fa-phone fa-2x"></i>
            </span>
            <h3 className="title is-5 mt-3">Phone</h3>
            <p>+263 77 123 4567</p>
          </div>

          <div className="column is-one-third has-text-centered">
            <span className="icon is-large has-text-primary">
              <i className="fas fa-map-marker-alt fa-2x"></i>
            </span>
            <h3 className="title is-5 mt-3">Office</h3>
            <p>Harare, Zimbabwe</p>
          </div>
        </div>

        {/* Contact Form */}
        <div className="box mt-6">
          <form>
            <div className="field">
              <label className="label">Name</label>
              <div className="control">
                <input className="input" type="text" placeholder="Your name" />
              </div>
            </div>

            <div className="field">
              <label className="label">Email</label>
              <div className="control">
                <input
                  className="input"
                  type="email"
                  placeholder="Your email address"
                />
              </div>
            </div>

            <div className="field">
              <label className="label">Message</label>
              <div className="control">
                <textarea
                  className="textarea"
                  placeholder="Write your message here..."
                ></textarea>
              </div>
            </div>

            <div className="field is-grouped is-justify-content-center">
              <div className="control">
                <button type="submit" className="button is-primary">
                  Send Message
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </section>
  );
}
