import React, { useState } from "react";
import axios from "axios";
import {
  DeleteFilled,
  PaperClipOutlined,
  LoadingOutlined,
} from "@ant-design/icons";

import OtherAvatars from "./OtherAvatars";
import { nowTimeStamp } from "../functions/dates";
import { getOtherUsers, getChatTitle } from "../functions/getOtherUsers";

import "../LiveChatHeader/ChatHeader.scss"; 

const ChatHeader = (props) => {
  const [isFilePickerLoading, setFilePickerLoading] = useState(false);
  const [isDeleteLoading, setDeleteLoading] = useState(false);

  const otherMembers = props.chat ? getOtherUsers(props.chat, props.username) : [];
  const otherMember = otherMembers.length > 0 ? otherMembers[0] : undefined;

  const onFilesSelect = (e) => {
    if (!props.chat) return;
    setFilePickerLoading(true);

    const headers = {
      "Project-ID": props.projectId,
      "User-Name": props.username,
      "User-Secret": props.secret,
    };

    const formdata = new FormData();
    const filesArr = Array.from(e.target.files || []);
    filesArr.forEach((file) => formdata.append("attachments", file, file.name));

    formdata.append("created", nowTimeStamp());
    formdata.append("sender_username", props.username);
    formdata.append("custom_json", JSON.stringify({}));

    axios
      .post(
        `https://api.chatengine.io/chats/${props.chat.id}/messages/`,
        formdata,
        { headers }
      )
      .then(() => setFilePickerLoading(false))
      .catch(() => setFilePickerLoading(false));
  };

  const onDelete = () => {
    if (!props.chat) return;
    setDeleteLoading(true);

    const headers = {
      "Project-ID": props.projectId,
      "User-Name": props.username,
      "User-Secret": props.secret,
    };

    axios
      .delete(`https://api.chatengine.io/chats/${props.chat.id}/`, { headers })
      .then((r) => {
        setDeleteLoading(false);
        props.onDeleteChat(r.data);
      });
  };

  if (!otherMember || !props.chat)
    return <div className="ce-custom-chat-header" />;

  return (
    <div className="ce-custom-chat-header">
      <OtherAvatars chat={props.chat} username={props.username} />

      <div className="ce-custom-header-text">
        <div className="ce-custom-header-title">
          {getChatTitle(props.chat, props.username)}
        </div>
        <div className="ce-custom-header-subtitle">
          {otherMember.is_online ? "Online" : "Offline"}
        </div>
      </div>

      <div className="ce-custom-header-icon-wrapper">
        <form style={{ display: "inline-block" }}>
          <label htmlFor="ce-files-picker">
            {isFilePickerLoading ? (
              <LoadingOutlined className="ce-custom-header-icon loading" />
            ) : (
              <PaperClipOutlined className="ce-custom-header-icon" />
            )}
          </label>
          <input
            multiple
            id="ce-files-picker"
            type="file"
            onChange={onFilesSelect}
            style={{ visibility: "hidden", height: "0px", width: "0px" }}
          />
        </form>

        {isDeleteLoading ? (
          <LoadingOutlined className="ce-custom-header-icon loading" />
        ) : (
          <DeleteFilled
            onClick={onDelete}
            className="ce-custom-header-icon"
          />
        )}
      </div>
    </div>
  );
};

export default ChatHeader;
