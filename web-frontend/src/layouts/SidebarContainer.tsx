import React, {MouseEventHandler} from "react";
import {NavLink} from "react-router-dom";

import {Nav} from "reactstrap";
import {route} from "../routes";

interface SidebarProps {
    location: Location
    toggleSidebar: MouseEventHandler<HTMLAnchorElement>
    routes: route[]
}

const SidebarContainer = (props: SidebarProps) => {

    const sideBarRef: React.RefObject<HTMLDivElement> = React.createRef()

    const activeRoute = (routeName: string) => {
        return props.location.pathname.indexOf(routeName) > -1 ? "active" : "";
    }

    activeRoute.bind(this);

    return (
        <div className="sidebar">
            <div className="sidebar-wrapper" ref={sideBarRef}>
                <Nav>
                    {props.routes.map((prop, key) => {
                        if (prop.hidden) return null;
                        return (
                            <li
                                className={activeRoute(prop.path)}
                                key={key}
                            >
                                <NavLink
                                    to={prop.layout + prop.path}
                                    className="nav-link"
                                    activeClassName="active"
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

export default SidebarContainer;
