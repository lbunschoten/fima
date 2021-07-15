import React from "react";
import ReactDOM from "react-dom";
import {HashRouter, Redirect, Route, Switch} from "react-router-dom";

import AdminLayout from "./layouts/Admin/Admin";

import "./assets/scss/black-dashboard-react.scss";
import "./assets/css/nucleo-icons.css";

ReactDOM.render(
    <HashRouter>
        <Switch>
            <Route path="/admin" render={props => <AdminLayout {...props as any} />} />
            <Redirect from="/" to="/admin/dashboard" />
        </Switch>
    </HashRouter>,
    document.getElementById("root")
);
