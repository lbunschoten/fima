import DashboardContainer from "./views/DashboardContainer";
import SubscriptionContainer from "./views/SubscriptionContainer";
import SubscriptionsContainer from "./views/SubscriptionsContainer";
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
    }
];
export default routes;
