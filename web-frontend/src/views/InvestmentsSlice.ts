import {AnyAction, createSlice, PayloadAction, ThunkAction} from "@reduxjs/toolkit";
import {RootState} from "../index";
import {Sector} from "../variables/Sector";
import {Position} from "../variables/Position";
import _ from "lodash";

export interface Sectors {
    [key: string]: Sector
}

export interface InvestmentsState {
    positions: Position[],
    sectors: Sectors
}

const initialState: InvestmentsState = {
    positions: [],
    sectors: {}
}

export const investmentsSlice = createSlice({
    name: 'investments',
    initialState,
    reducers: {
        setPositions: (state, action: PayloadAction<Position[]>) => {
            state.positions = action.payload
        },
        setSectors: (state, action: PayloadAction<Sectors>) => {
            state.sectors = action.payload
        }
    }
})

export const {setPositions, setSectors} = investmentsSlice.actions

export const fetchPositions = (): ThunkAction<void, RootState, unknown, AnyAction> => async dispatch => {
    const asyncResp = await fetch(`${process.env.REACT_APP_INVESTMENT_API_HOST}/stocks`)
    const positions: Position[] = await asyncResp.json()
    dispatch(setPositions(positions))
}

export const fetchSectors = (): ThunkAction<void, RootState, unknown, AnyAction> => async dispatch => {
    const asyncResp = await fetch(`${process.env.REACT_APP_INVESTMENT_API_HOST}/sectors`)
    const sectors: Sector[] = await asyncResp.json()
    const sectorsByType = _.chain(sectors).groupBy('type').mapValues(s => s[0]).value()
    dispatch(setSectors(sectorsByType))
}