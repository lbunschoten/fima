import {AnyAction, createSlice, PayloadAction, ThunkAction} from "@reduxjs/toolkit";
import {SubscriptionData} from "../variables/SubscriptionData";
import {RootState} from "../index";
import {TransactionData} from "../variables/TransactionData";

interface SubscriptionState {
    subscription: SubscriptionData | null,
    transactions: TransactionData[]
}


const initialState: SubscriptionState = {
    subscription: null,
    transactions: []
}

export const subscriptionSlice = createSlice({
    name: 'subscription',
    initialState,
    reducers: {
        setSubscriptionTransactions: (state, action: PayloadAction<SubscriptionTransactionsResponse>) => {
            return {
                ...state,
                subscription: action.payload.subscription,
                transactions: action.payload.transactions
            }
        },
    }
})

interface SubscriptionTransactionsResponse {
    subscription: SubscriptionData | null,
    transactions: TransactionData[]
}

export const {setSubscriptionTransactions} = subscriptionSlice.actions

export const fetchTransactions = (subscriptionId: string): ThunkAction<void, RootState, unknown, AnyAction> => async dispatch => {
    const asyncResp = await fetch(`${process.env.REACT_APP_API_HOST}/subscription/subscription/${subscriptionId}`)
    const response: SubscriptionTransactionsResponse = await asyncResp.json()
    dispatch(setSubscriptionTransactions(response))
}
