import DashboardContainer from "./views/DashboardContainer";
import IconsContainer from "./views/IconsContainer";
import NotificationsContainer from "./views/NotificationsContainer";
import SubscriptionContainer from "./views/SubscriptionContainer";
import SubscriptionsContainer from "./views/SubscriptionsContainer";
import TypographyContainer from "./views/TypographyContainer";
import UserProfileContainer from "./views/UserProfileContainer";
import InvestmentsContainer from "./views/InvestmentsContainer";

export type route = {
    path: string,
    name: string,
    icon?: string,
    component: (props: any) => JSX.Element
    layout: string,
    hidden?: boolean,
}

let routes: route[] = [
    {
        path: "/dashboard",
        name: "Dashboard",
        icon: "tim-icons icon-chart-pie-36",
        component: DashboardContainer,
        layout: "/admin"
    },
    {
        path: "/investments",
        name: "Investments",
        icon: "tim-icons icon-atom",
        component: InvestmentsContainer,
        layout: "/admin"
    },
    {
        path: "/icons",
        name: "Icons",
        icon: "tim-icons icon-atom",
        component: IconsContainer,
        layout: "/admin"
    },
    {
        path: "/notifications",
        name: "Notifications",
        icon: "tim-icons icon-bell-55",
        component: NotificationsContainer,
        layout: "/admin"
    },
    {
        path: "/user-profile",
        name: "User Profile",
        icon: "tim-icons icon-single-02",
        component: UserProfileContainer,
        layout: "/admin"
    },
    {
        path: "/subscriptions",
        name: "Subscriptions",
        icon: "tim-icons icon-puzzle-10",
        component: SubscriptionsContainer,
        layout: "/admin",
    },
    {
        hidden: true,
        path: "/subscription/:id",
        name: "Subscription",
        component: SubscriptionContainer,
        layout: "/admin"
    },
    {
        path: "/typography",
        name: "Typography",
        icon: "tim-icons icon-align-center",
        component: TypographyContainer,
        layout: "/admin"
    }
];
export default routes;
