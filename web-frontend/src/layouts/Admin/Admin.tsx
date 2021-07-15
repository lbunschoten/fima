import React from "react";
import {Route, Switch} from "react-router-dom";

import AdminNavbar from "../../components/Navbars/AdminNavbar";
import Sidebar from "../../components/Sidebar/Sidebar";

import routes, {route} from "../../routes";
import {BrowserHistory} from "history";

export interface AdminProps {
  location: Location,
  history: BrowserHistory
}
interface AdminState {
  sidebarOpened: boolean,
}

class Admin extends React.Component<AdminProps, AdminState> {
  mainPanelRef: React.RefObject<HTMLDivElement> = React.createRef()
  
  constructor(props: AdminProps) {
    super(props);

    this.state = {
      sidebarOpened: document.documentElement.className.indexOf("nav-open") !== -1,
    };
  }
  componentDidUpdate<P extends AdminProps, S extends AdminState, SS>(prevProps: P, prevState: S, snapshot: SS) {
    if (prevProps.history.action === "PUSH") {
      document.documentElement.scrollTop = 0;
      if (document.scrollingElement) {
        document.scrollingElement.scrollTop = 0;
      }

      if (this.mainPanelRef.current) {
        this.mainPanelRef.current.scrollTop = 0
      }
    }
  }
  // this function opens and closes the sidebar on small devices
  toggleSidebar = () => {
    document.documentElement.classList.toggle("nav-open");
    this.setState({ sidebarOpened: !this.state.sidebarOpened });
  };
  getRoutes = (routes: route[]) => {
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
  brandText = (path: string) => {
    for (let i = 0; i < routes.length; i++) {
      if (
        path.indexOf(
          routes[i].layout + routes[i].path
        ) !== -1
      ) {
        return routes[i].name;
      }
    }
    return "Brand";
  };
  render() {
    return (
      <>
        <div className="wrapper">
          <Sidebar
            {...this.props}
            routes={routes}
            toggleSidebar={this.toggleSidebar}
          />
          <div
            className="main-panel"
            ref={this.mainPanelRef}
          >
            <AdminNavbar
              {...this.props}
              brandText={this.brandText(this.props.location.pathname)}
              toggleSidebar={this.toggleSidebar}
              sidebarOpened={this.state.sidebarOpened}
          />
            <Switch>{this.getRoutes(routes)}</Switch>
          </div>
        </div>
      </>
    );
  }
}

export default Admin;
