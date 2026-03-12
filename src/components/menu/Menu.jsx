import { Link } from "react-router-dom";
import "../menu/menu.scss";
import { menu } from "../../data";

const Menu = ({ collapsed }) => {
  return (
    <div className="menu">
      {menu.map((item) => (
        <div className={collapsed ? "item collapsed" : "item"} key={item.id}>
          <span className="title">{item.title}</span>
          {item.listItems.map((listItem) => (
            <Link
              to={listItem.url}
              className={collapsed ? "listItem collapsed" : "listItem"}
              key={listItem.id}
            >
              <img src={listItem.icon?.startsWith('/') ? listItem.icon : `/${listItem.icon}`} alt="" />
              <span className="listItemTitle">{listItem.title}</span>
            </Link>
          ))}
        </div>
      ))}
    </div>
  );
};

export default Menu;
