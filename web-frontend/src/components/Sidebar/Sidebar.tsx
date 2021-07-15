import React, {MouseEventHandler} from "react";
import {NavLink} from "react-router-dom";

import {Nav} from "reactstrap";
import {route} from "../../routes";

interface SidebarProps {
    location: Location
    toggleSidebar: MouseEventHandler<HTMLAnchorElement>
    routes: route[]
}

interface SidebarState {
}

class Sidebar extends React.Component<SidebarProps, SidebarState> {

    sideBarRef: React.RefObject<HTMLDivElement> = React.createRef()

    constructor(props: SidebarProps) {
        super(props);
        this.activeRoute.bind(this);
    }

    // verifies if routeName is the one active (in browser input)
    activeRoute(routeName: string) {
        return this.props.location.pathname.indexOf(routeName) > -1 ? "active" : "";
    }

    render() {
        const {routes} = this.props;
        return (
            <div className="sidebar">
                <div className="sidebar-wrapper" ref={this.sideBarRef}>
                    <Nav>
                        {routes.map((prop, key) => {
                            if (prop.hidden) return null;
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

export default Sidebar;
