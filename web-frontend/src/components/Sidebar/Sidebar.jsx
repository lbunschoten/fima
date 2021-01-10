/*eslint-disable*/
import React from "react";
import { NavLink } from "react-router-dom";
// nodejs library to set properties for components
import PropTypes from "prop-types";

// reactstrap components
import { Nav } from "reactstrap";

class Sidebar extends React.Component {
  constructor(props) {
    super(props);
    this.activeRoute.bind(this);
    this.sideBarRef = React.createRef();
  }
  // verifies if routeName is the one active (in browser input)
  activeRoute(routeName) {
    return this.props.location.pathname.indexOf(routeName) > -1 ? "active" : "";
  }
  render() {
    const { routes } = this.props;
    return (
      <div className="sidebar">
        <div className="sidebar-wrapper" ref={this.sideBarRef}>
          <Nav>
            {routes.map((prop, key) => {
              if (prop.redirect) return null;
              return (
                <li
                  className={this.activeRoute(prop.path)}
                  key={key}
                >
                  <NavLink
                    to={prop.layout + prop.path}
                    className="nav-link"
                    activeClassName="active"
                    onClick={this.props.toggleSidebar}
                  >
                    <i className={prop.icon} />
                    <p>{prop.name}</p>
                  </NavLink>
                </li>
              );
            })}
          </Nav>
        </div>
      </div>
    );
  }
}

Sidebar.defaultProps = {
  routes: [{}]
};

Sidebar.propTypes = {
  routes: PropTypes.arrayOf(PropTypes.object)
};

export default Sidebar;
