import { useState, useRef, useEffect } from "react";
import "./chat.scss";
import EmojiPicker from "emoji-picker-react";

const Chat = () => {

  const [open, setOpen] = useState(false);
  const [text, setText] = useState("");
  const [messages, setMessages] = useState([
    {
      id: 1,
      text: "Hello there!",
      own: false,
      time: "Just now"
    },
    {
      id: 2,
      text: "Hi! How are you?",
      own: true,
      time: "Just now"
    }
  ]);

  const endRef = useRef(null);

  
  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleEmoji = (emojiData) => {
    setText((prev) => prev + emojiData.emoji);
  };

  const handleSend = () => {
    if (text.trim() === "") return;

    const newMessage = {
      id: Date.now(),
      text: text,
      own: true,
      time: "Just now"
    };

    setMessages((prev) => [...prev, newMessage]);
    setText("");
  };

  return (
    <div className="chat">

      {/* Top Bar */}
      <div className="top">
        <div className="user">
          <img src="/avatar.png" alt="avatar" />
          <div className="texts">
            <span>Jane Doe</span>
            <p>Community Farmer Chat</p>
          </div>
        </div>

        <div className="icons">
          <img src="/phone.png" alt="phone" />
          <img src="/video.png" alt="video" />
          <img src="/info.png" alt="info" />
        </div>
      </div>

      {/* Messages */}
      <div className="center">

        {messages.map((msg) => (
          <div key={msg.id} className={`message ${msg.own ? "own" : ""}`}>

            {!msg.own && <img src="/avatar.png" alt="avatar" />}

            <div className="text">
              <p>{msg.text}</p>
              <span>{msg.time}</span>
            </div>

          </div>
        ))}

        <div ref={endRef}></div>

      </div>

      {/* Bottom Input */}
      <div className="bottom">

        <div className="icons">
          <img src="/img.png" alt="image" />
          <img src="/camera.png" alt="camera" />
          <img src="/mic.png" alt="mic" />
        </div>

        <input
          type="text"
          placeholder="Type a message..."
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
        />

        <div className="emoji">

          <img
            src="/emoji.png"
            alt="emoji"
            onClick={() => setOpen((prev) => !prev)}
          />

          {open && (
            <div className="picker">
              <EmojiPicker onEmojiClick={handleEmoji} />
            </div>
          )}

        </div>

        <button className="sendButton" onClick={handleSend}>
          Send
        </button>

      </div>

    </div>
  );
};

export default Chat;