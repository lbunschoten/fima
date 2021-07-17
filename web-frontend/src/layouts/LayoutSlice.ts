import {createSlice} from "@reduxjs/toolkit";

export interface AdminLayoutState {
    sidebarOpened: boolean,
    collapseOpen: boolean,
    modalSearchOpen: boolean,
}

const initialState: AdminLayoutState = {
    sidebarOpened: document.documentElement.className.indexOf("nav-open") !== -1,
    collapseOpen: false,
    modalSearchOpen: false,
}

export const layoutSlice = createSlice({
    name: 'admin-layout',
    initialState,
    reducers: {
        toggleSidebar: (state) => {
          document.documentElement.classList.toggle("nav-open");
          state.sidebarOpened = !state.sidebarOpened
        },
        toggleTopBar: (state) => {
            state.collapseOpen = !state.collapseOpen
        },
        toggleModalSearch: (state) => {
            state.modalSearchOpen = !state.modalSearchOpen
        }
    }
})

export const {toggleSidebar, toggleTopBar, toggleModalSearch} = layoutSlice.actions
