import {AnyAction, createSlice, PayloadAction, ThunkAction} from "@reduxjs/toolkit";
import {SubscriptionData} from "../variables/SubscriptionData";
import {RootState} from "../index";

interface SubscriptionsState {
    subscriptions: SubscriptionData[],
}


const initialState: SubscriptionsState = {
    subscriptions: []
}


export const subscriptionsSlice = createSlice({
    name: 'subscriptions',
    initialState,
    reducers: {
        setSubscriptions: (state, action: PayloadAction<SubscriptionData[]>) => {
            state.subscriptions = action.payload
        },
    }
})

export const {setSubscriptions} = subscriptionsSlice.actions

export const fetchSubscriptions = (): ThunkAction<void, RootState, unknown, AnyAction> => async dispatch => {
    const asyncResp = await fetch(`${process.env.REACT_APP_API_HOST}/subscriptions`)
    const subscriptions: SubscriptionData[] = await asyncResp.json()
    dispatch(setSubscriptions(subscriptions))
}
