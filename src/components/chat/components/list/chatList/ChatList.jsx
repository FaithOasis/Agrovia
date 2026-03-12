import { useState } from "react";
import "./chatlist.scss";

const ChatList = () => {
  // Controls the quick add chat form visibility
  const [addMode, setAddMode] = useState(false);

  // Stores the current search term
  const [search, setSearch] = useState("");

  // Tracks the currently selected conversation
  const [activeChatId, setActiveChatId] = useState(2);

  // Demo chat data for the sidebar list
  const chats = [
    {
      id: 1,
      name: "Jane Doe",
      message: "Hello there",
      time: "9:20 PM",
      unread: 0,
      online: true,
    },
    {
      id: 2,
      name: "Michael Smith",
      message: "How are the crops?",
      time: "9:18 PM",
      unread: 2,
      online: true,
    },
    {
      id: 3,
      name: "Sarah Lee",
      message: "Weather looks good today",
      time: "8:54 PM",
      unread: 0,
      online: false,
    },
    {
      id: 4,
      name: "Agri Support",
      message: "The field report was submitted",
      time: "8:10 PM",
      unread: 1,
      online: false,
    },
  ];

  // Filters chats by name
  const filteredChats = chats.filter((chat) =>
    chat.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="chatlist">
      {/* Search and quick-add area */}
      <div className="search">
        <div className="searchbar">
          <img src="/search.png" alt="search" />
          <input
            type="text"
            placeholder="Search or start a new chat"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <button
          className="add"
          onClick={() => setAddMode((prev) => !prev)}
          aria-label="toggle add chat"
        >
          {addMode ? "−" : "+"}
        </button>
      </div>

      {/* Add new user area */}
      {addMode && (
        <div className="addUser">
          <input type="text" placeholder="Enter username..." />
          <button>Add</button>
        </div>
      )}

      {/* Chat conversation list */}
      <div className="items">
        {filteredChats.map((chat) => (
          <div
            className={`item ${activeChatId === chat.id ? "active" : ""}`}
            key={chat.id}
            onClick={() => setActiveChatId(chat.id)}
          >
            <div className="avatarWrap">
              <div className="avatar">{chat.name.charAt(0)}</div>
              {chat.online && <span className="onlineDot"></span>}
            </div>

            <div className="texts">
              <div className="topRow">
                <span className="name">{chat.name}</span>
                <span className="time">{chat.time}</span>
              </div>

              <div className="bottomRow">
                <p>{chat.message}</p>
                {chat.unread > 0 && <span className="badge">{chat.unread}</span>}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ChatList;