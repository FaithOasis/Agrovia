import { useState } from "react";
import List from "../../components/chat/components/list/List";
import Chat from "../../components/chat/components/chat/Chat";
import Detail from "../../components/chat/components/detail/Detail";
import "./community.scss";

const Community = () => {

  // Controls showing / hiding the right detail panel
  const [showDetail, setShowDetail] = useState(true);

  return (
    <div className="community">

      {/* LEFT SIDEBAR (Chat list) */}
      <div className="community__sidebar">
        <List />
      </div>

      {/* MAIN CHAT */}
      <div className="community__chat">
        <Chat toggleDetail={() => setShowDetail(!showDetail)} />
      </div>

      {/* RIGHT DETAIL PANEL */}
      {showDetail && (
        <div className="community__detail">
          <Detail />
        </div>
      )}

    </div>
  );
};

export default Community;