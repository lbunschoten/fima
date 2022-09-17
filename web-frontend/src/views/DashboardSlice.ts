import {AnyAction, createSlice, PayloadAction, ThunkAction} from "@reduxjs/toolkit";
import {TransactionData} from "../variables/TransactionData";
import {SubscriptionData} from "../variables/SubscriptionData";
import {ChartOptions} from "chart.js";
import {RootState} from "../index";
import {transactionsChart} from "../variables/charts";

export type DashboardChart = {
    label: string,
    results: any[],
    options: ChartOptions<any>
}

export interface DashboardState {
    selectedChart: string,
    countChart: DashboardChart,
    sumChart: DashboardChart,
    balanceChart: DashboardChart,
    transactions: TransactionData[],
    subscriptions: SubscriptionData[],
}

const initialState: DashboardState = {
    selectedChart: "count",
    countChart: {
        label: "# of transactions",
        results: [],
        options: transactionsChart.defaultChartOptions
    },
    sumChart: {
        label: "Sum of transactions",
        results: [],
        options: transactionsChart.currencyChartOptions
    },
    balanceChart: {
        label: "Balance",
        results: [],
        options: transactionsChart.currencyChartOptions
    },
    transactions: [],
    subscriptions: []
}

export const dashboardSlice = createSlice({
    name: 'dashboard',
    initialState,
    reducers: {
        setSelectedChart: (state, action: PayloadAction<string>) => {
            state.selectedChart = action.payload
        },
        setTransactions: (state, action: PayloadAction<TransactionData[]>) => {
            state.transactions = action.payload
        },
        setSubscriptions: (state, action: PayloadAction<SubscriptionData[]>) => {
            state.subscriptions = action.payload
        },
        setCountChartData: (state, action: PayloadAction<any[]>) => {
            state.countChart.results = action.payload
        },
        setSumChart: (state, action: PayloadAction<any[]>) => {
            state.sumChart.results = action.payload
        },
        setBalanceChart: (state, action: PayloadAction<any[]>) => {
            state.balanceChart.results = action.payload
        },
    }
})

export const {setSelectedChart, setTransactions, setSubscriptions, setCountChartData, setSumChart, setBalanceChart} = dashboardSlice.actions

export const fetchTransactions = (): ThunkAction<void, RootState, unknown, AnyAction> => async dispatch => {
    const asyncResp = await fetch(`${process.env.REACT_APP_API_HOST}/transaction/transaction/recent?offset=0&limit=10`)
    const transactions: TransactionData[] = await asyncResp.json()
    dispatch(setTransactions(transactions))
}

export const fetchSubscriptions = (): ThunkAction<void, RootState, unknown, AnyAction> => async dispatch => {
    const asyncResp = await fetch(`${process.env.REACT_APP_API_HOST}/subscription/subscriptions`)
    const subscriptions: SubscriptionData[] = await asyncResp.json()
    dispatch(setSubscriptions(subscriptions))
}

interface TransactionStatistics {
    transactions: DashboardChart,
    sum: DashboardChart,
    balance: DashboardChart
}

export const fetchTransactionStatistics = (): ThunkAction<void, RootState, unknown, AnyAction> => async dispatch => {
    const asyncResp = await fetch(`${process.env.REACT_APP_API_HOST}/transaction/transaction/statistics`)
    const results: TransactionStatistics[] = await asyncResp.json()
    dispatch(setCountChartData(results.map(stats => stats.transactions)))
    dispatch(setSumChart(results.map(stats => stats.sum)))
    dispatch(setBalanceChart(results.map(stats => stats.balance)))
}