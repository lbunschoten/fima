import Dashboard from "./views/Dashboard";
import Icons from "./views/Icons";
import Notifications from "./views/Notifications";
import Subscription from "./views/Subscription";
import Subscriptions from "./views/Subscriptions";
import Typography from "./views/Typography";
import UserProfile from "./views/UserProfile";
import React from "react";

export type route = {
    path: string,
    name: string,
    icon?: string,
    component: typeof React.Component
    layout: string,
    hidden?: boolean,
}

let routes: route[] = [
    {
        path: "/dashboard",
        name: "Dashboard",
        icon: "tim-icons icon-chart-pie-36",
        component: Dashboard,
        layout: "/admin"
    },
    {
        path: "/icons",
        name: "Icons",
        icon: "tim-icons icon-atom",
        component: Icons,
        layout: "/admin"
    },
    {
        path: "/notifications",
        name: "Notifications",
        icon: "tim-icons icon-bell-55",
        component: Notifications,
        layout: "/admin"
    },
    {
        path: "/user-profile",
        name: "User Profile",
        icon: "tim-icons icon-single-02",
        component: UserProfile,
        layout: "/admin"
    },
    {
        path: "/subscriptions",
        name: "Subscriptions",
        icon: "tim-icons icon-puzzle-10",
        component: Subscriptions,
        layout: "/admin",
    },
    {
        hidden: true,
        path: "/subscription/:id",
        name: "Subscription",
        component: Subscription,
        layout: "/admin"
    },
    {
        path: "/typography",
        name: "Typography",
        icon: "tim-icons icon-align-center",
        component: Typography,
        layout: "/admin"
    }
];
export default routes;
