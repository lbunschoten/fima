import React from "react";
import ReactDOM from "react-dom";
import { HashRouter, Route, Switch, Redirect } from "react-router-dom";

import AdminLayout from "./layouts/Admin/Admin.jsx";

import "./assets/scss/black-dashboard-react.scss";
import "./assets/css/nucleo-icons.css";

ReactDOM.render(
  <HashRouter>
    <Switch>
      <Route path="/admin" render={props => <AdminLayout {...props} />} />
      <Redirect from="/" to="/admin/dashboard" />
    </Switch>
  </HashRouter>,
  document.getElementById("root")
);
