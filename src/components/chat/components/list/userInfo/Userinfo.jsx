import "./userinfo.scss";

const Userinfo = () => {
  return (
    <div className="userinfo">
      {/* Current logged-in user preview */}
      <div className="user">
        <div className="avatarWrapper">
          <img src="/avatar.png" alt="User avatar" />
          <span className="status"></span>
        </div>

        <div className="details">
          <h3>John Doe</h3>
          <p>Online</p>
        </div>
      </div>

      {/* Sidebar quick actions */}
      <div className="actions">
        <button className="iconBtn" aria-label="More options">
          <img src="/more.png" alt="More options" />
        </button>

        <button className="iconBtn" aria-label="Start video call">
          <img src="/video.png" alt="Start video call" />
        </button>

        <button className="iconBtn" aria-label="Edit profile">
          <img src="/edit.png" alt="Edit profile" />
        </button>
      </div>
    </div>
  );
};

export default Userinfo;