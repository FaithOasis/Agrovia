import { useState } from "react";
import "./detail.scss";

const sharedPhotos = [
  { id: 1, src: "/corn1.jpg", name: "corn1.jpg" },
  { id: 2, src: "/corn2.jpg", name: "corn2.jpg" },
  { id: 3, src: "/seed.webp", name: "seed.webp" },
  { id: 4, src: "/bg.jpg", name: "bg.jpg" },
];

const Detail = () => {
  // Controls each collapsible section independently
  const [openPhotos, setOpenPhotos] = useState(true);
  const [openSettings, setOpenSettings] = useState(false);
  const [openPrivacy, setOpenPrivacy] = useState(false);
  const [openFiles, setOpenFiles] = useState(false);

  return (
    <div className="detail">
      {/* Contact / user profile summary */}
      <div className="user">
        <img src="/avatar.png" alt="user avatar" />
        <h2>Jane Doe</h2>
        <p>Crop advisor • Harare Region</p>
      </div>

      {/* Scrollable information area */}
      <div className="info">
        {/* Chat settings section */}
        <div className="option">
          <div className="title" onClick={() => setOpenSettings(!openSettings)}>
            <span>Chat Settings</span>
            <img
              src={openSettings ? "/arrowDown.png" : "/arrowUp.png"}
              alt="toggle chat settings"
            />
          </div>

          {openSettings && (
            <div className="content">
              <p>Mute notifications</p>
              <p>Pin conversation</p>
              <p>Star messages</p>
            </div>
          )}
        </div>

        {/* Privacy and support options */}
        <div className="option">
          <div className="title" onClick={() => setOpenPrivacy(!openPrivacy)}>
            <span>Privacy & Help</span>
            <img
              src={openPrivacy ? "/arrowDown.png" : "/arrowUp.png"}
              alt="toggle privacy and help"
            />
          </div>

          {openPrivacy && (
            <div className="content">
              <p>Report user</p>
              <p>Help center</p>
              <p>Encryption info</p>
            </div>
          )}
        </div>

        {/* Shared media preview */}
        <div className="option">
          <div className="title" onClick={() => setOpenPhotos(!openPhotos)}>
            <span>Shared Photos</span>
            <img
              src={openPhotos ? "/arrowDown.png" : "/arrowUp.png"}
              alt="toggle shared photos"
            />
          </div>

          {openPhotos && (
            <div className="photos">
              {sharedPhotos.map((photo) => (
                <div className="photoItem" key={photo.id}>
                  <div className="photoDetail">
                    <img src={photo.src} alt={photo.name} />
                    <span>{photo.name}</span>
                  </div>

                  <img
                    src="/download.png"
                    alt="download file"
                    className="icon"
                  />
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Shared files section */}
        <div className="option">
          <div className="title" onClick={() => setOpenFiles(!openFiles)}>
            <span>Shared Files</span>
            <img
              src={openFiles ? "/arrowDown.png" : "/arrowUp.png"}
              alt="toggle shared files"
            />
          </div>

          {openFiles && (
            <div className="content">
              <p>No files shared yet</p>
            </div>
          )}
        </div>

        {/* Chat-level actions */}
        <div className="actions">
          <button className="block">Block User</button>
          <button className="clear">Clear Chat</button>
        </div>
      </div>
    </div>
  );
};

export default Detail;