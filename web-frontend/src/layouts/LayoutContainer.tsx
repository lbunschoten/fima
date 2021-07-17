import React from "react";
import {Route, Switch} from "react-router-dom";

import TopBarContainer from "./TopBarContainer";
import SidebarContainer from "./SidebarContainer";

import routes, {route} from "../routes";
import {BrowserHistory} from "history";
import {useAppDispatch, useAppSelector} from "../index";
import {toggleSidebar} from "./LayoutSlice";

export interface AdminProps {
    location: Location,
    history: BrowserHistory
}

const LayoutContainer = (props: AdminProps) => {
    const dispatch = useAppDispatch()

    const mainPanelRef: React.RefObject<HTMLDivElement> = React.createRef()
    const sidebarOpened = useAppSelector((state) => state.layout.sidebarOpened)

    const getRoutes = (routes: route[]) => {
        return routes.map((prop, key) => {
            if (prop.layout === "/admin") {
                return (
                    <Route
                        path={prop.layout + prop.path}
                        component={prop.component}
                        key={key}
                    />
                );
            } else {
                return null;
            }
        });
    };

    const brandText = (path: string) => {
        for (let i = 0; i < routes.length; i++) {
            if (path.indexOf(routes[i].layout + routes[i].path) !== -1) {
                return routes[i].name;
            }
        }
        return "Brand";
    };

    return (
        <>
            <div className="wrapper">
                <SidebarContainer
                    {...props}
                    routes={routes}
                    toggleSidebar={() => dispatch(toggleSidebar())}
                />
                <div
                    className="main-panel"
                    ref={mainPanelRef}
                >
                    <TopBarContainer
                        {...props}
                        brandText={brandText(props.location.pathname)}
                        toggleSidebar={() => dispatch(toggleSidebar())}
                        sidebarOpened={sidebarOpened}
                    />
                    <Switch>{getRoutes(routes)}</Switch>
                </div>
            </div>
        </>
    );
}

export default LayoutContainer;