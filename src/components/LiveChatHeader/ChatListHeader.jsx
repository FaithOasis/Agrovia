import React from "react";
import { FormOutlined } from "@ant-design/icons";
import "./ChatListHeader.scss"; // separate SCSS file

const ChatListHeader = ({ onNewChatClick }) => {
  return (
    <div className="ce-chat-list-header">
      <div className="ce-chat-list-header-title">Chats</div>

      <button
        className="ce-new-chat-button"
        onClick={onNewChatClick}
      >
        <FormOutlined />
      </button>
    </div>
  );
};

export default ChatListHeader;
