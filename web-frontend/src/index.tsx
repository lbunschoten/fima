import React from "react";
import ReactDOM from "react-dom";
import {HashRouter, Redirect, Route, Switch} from "react-router-dom";
import {configureStore, getDefaultMiddleware} from '@reduxjs/toolkit'

import LayoutContainer from "./layouts/LayoutContainer";

import "./assets/scss/black-dashboard-react.scss";
import "./assets/css/nucleo-icons.css";
import {Provider, TypedUseSelectorHook, useDispatch, useSelector} from "react-redux";
import {dashboardSlice} from "./views/DashboardSlice";
import {subscriptionsSlice} from "./views/SubscriptionsSlice";
import {subscriptionSlice} from "./views/SubscriptionSlice";
import {layoutSlice} from "./layouts/LayoutSlice";
import {investmentsSlice} from "./views/InvestmentsSlice";


const store = configureStore({
    reducer: {
        dashboard: dashboardSlice.reducer,
        subscription: subscriptionSlice.reducer,
        subscriptions: subscriptionsSlice.reducer,
        layout: layoutSlice.reducer,
        investments: investmentsSlice.reducer
    },
    middleware: getDefaultMiddleware({
        serializableCheck: false,
    }),
})

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch

// Use throughout your app instead of plain `useDispatch` and `useSelector`
export const useAppDispatch = () => useDispatch<AppDispatch>()
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector

ReactDOM.render(
    [
        <Provider store={store}>
            <HashRouter>
                <Switch>
                    <Route path="/admin" render={props => <LayoutContainer {...props as any} />} />
                    <Redirect from="/" to="/admin/dashboard" />
                </Switch>
            </HashRouter>
        </Provider>,

    ],
    document.getElementById("root")
);
