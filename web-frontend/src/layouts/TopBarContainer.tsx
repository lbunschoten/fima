import React from "react";
import classNames from "classnames";

import {
    Button,
    Collapse,
    Container,
    DropdownItem,
    DropdownMenu,
    DropdownToggle,
    Input,
    InputGroup,
    Modal,
    Nav,
    Navbar,
    NavbarBrand,
    NavLink,
    UncontrolledDropdown
} from "reactstrap";

import anime3 from "../assets/img/anime3.png";
import {useAppDispatch, useAppSelector} from "../index";
import {toggleModalSearch, toggleTopBar} from "./LayoutSlice";

interface AdminNavbarProps {
    sidebarOpened: boolean,
    brandText: string,
    toggleSidebar: () => void
}

const TopBarContainer = (props: AdminNavbarProps) => {

    const dispatch = useAppDispatch()
    const collapseOpen = useAppSelector((state => state.layout.collapseOpen))
    const modalSearchOpen = useAppSelector((state => state.layout.modalSearchOpen))

    return (
        <>
            <Navbar
                className={classNames("navbar-absolute")}
                expand="lg"
            >
                <Container fluid>
                    <div className="navbar-wrapper">
                        <div
                            className={classNames("navbar-toggle d-inline", {
                                toggled: props.sidebarOpened
                            })}
                        >
                            <button
                                className="navbar-toggler"
                                type="button"
                                onClick={props.toggleSidebar}
                            >
                                <span className="navbar-toggler-bar bar1" />
                                <span className="navbar-toggler-bar bar2" />
                                <span className="navbar-toggler-bar bar3" />
                            </button>
                        </div>
                        <NavbarBrand href="#" onClick={e => e.preventDefault()}>
                            Finance manager - {props.brandText}
                        </NavbarBrand>
                    </div>
                    <button
                        aria-expanded={false}
                        aria-label="Toggle navigation"
                        className="navbar-toggler"
                        data-target="#navigation"
                        data-toggle="collapse"
                        id="navigation"
                        type="button"
                        onClick={() => dispatch(toggleTopBar())}
                    >
                        <span className="navbar-toggler-bar navbar-kebab" />
                        <span className="navbar-toggler-bar navbar-kebab" />
                        <span className="navbar-toggler-bar navbar-kebab" />
                    </button>
                    <Collapse navbar isOpen={collapseOpen}>
                        <Nav className="ml-auto" navbar>
                            <InputGroup className="search-bar">
                                <Button
                                    color="link"
                                    data-target="#searchModal"
                                    data-toggle="modal"
                                    id="search-button"
                                    onClick={() => dispatch(toggleModalSearch())}
                                >
                                    <i className="tim-icons icon-zoom-split" />
                                    <span className="d-lg-none d-md-block">Search</span>
                                </Button>
                            </InputGroup>
                            <UncontrolledDropdown nav>
                                <DropdownToggle
                                    caret
                                    color="default"
                                    data-toggle="dropdown"
                                    nav
                                    onClick={e => e.preventDefault()}
                                >
                                    <div className="photo">
                                        <img alt="..." src={anime3} />
                                    </div>
                                    <b className="caret d-none d-lg-block d-xl-block" />
                                    <p className="d-lg-none">Log out</p>
                                </DropdownToggle>
                                <DropdownMenu className="dropdown-navbar" right tag="ul">
                                    <NavLink tag="li">
                                        <DropdownItem className="nav-item">Profile</DropdownItem>
                                    </NavLink>
                                    <NavLink tag="li">
                                        <DropdownItem className="nav-item">Settings</DropdownItem>
                                    </NavLink>
                                    <DropdownItem divider tag="li" />
                                    <NavLink tag="li">
                                        <DropdownItem className="nav-item">Log out</DropdownItem>
                                    </NavLink>
                                </DropdownMenu>
                            </UncontrolledDropdown>
                            <li className="separator d-lg-none" />
                        </Nav>
                    </Collapse>
                </Container>
            </Navbar>
            <Modal
                modalClassName="modal-search"
                isOpen={modalSearchOpen}
                toggle={() => dispatch(toggleModalSearch())}
            >
                <div className="modal-header">
                    <Input id="inlineFormInputGroup" placeholder="SEARCH" type="text" />
                    <button
                        aria-label="Close"
                        className="close"
                        data-dismiss="modal"
                        type="button"
                        onClick={() => dispatch(toggleModalSearch())}
                    >
                        <i className="tim-icons icon-simple-remove" />
                    </button>
                </div>
            </Modal>
        </>
    );
}

export default TopBarContainer;
