import React, {useEffect} from "react";

import {Button, Card, CardBody, CardHeader, CardTitle, Col, DropdownItem, DropdownMenu, DropdownToggle, Row, Table, UncontrolledDropdown, UncontrolledTooltip} from "reactstrap";
import {useAppDispatch, useAppSelector} from "../index";
import {Link} from "react-router-dom";
import {fetchSectors, fetchPositions} from "./InvestmentsSlice";
import _ from "lodash";
import {Dictionary} from "@reduxjs/toolkit";
import {Position} from "../variables/Position";

const InvestmentsContainer = () => {

    const dispatch = useAppDispatch()
    const sectors = useAppSelector((state) => state.investments.sectors)
    const allPositions = useAppSelector((state) => state.investments.positions)
    const positionsBySector: Dictionary<Position[]> = useAppSelector((state) => _.chain(state.investments.positions).groupBy("stock.sector").value())
    const totalPositionValue = useAppSelector((state) => {
        return _.sum(state.investments.positions.map(p => p.shares * p.stock.market_value))
    })

    useEffect(() => {
        dispatch(fetchSectors())
    }, [dispatch])

    useEffect(() => {
        dispatch(fetchPositions())
    }, [dispatch])

    return (
        <>
            <div className="content">
                <Row>
                    <Col lg="6" md="12">
                        <Card className="card-tasks">
                            <CardHeader>
                                <h6 className="title d-inline">Stocks ({allPositions.length})</h6>
                                <UncontrolledDropdown>
                                    <DropdownToggle
                                        caret
                                        className="btn-icon"
                                        color="link"
                                        data-toggle="dropdown"
                                        type="button"
                                    >
                                        <i className="tim-icons icon-settings-gear-63" />
                                    </DropdownToggle>
                                    <DropdownMenu aria-labelledby="dropdownMenuLink" right>
                                        <DropdownItem
                                            href="#pablo"
                                            onClick={e => e.preventDefault()}
                                        >
                                            Action
                                        </DropdownItem>
                                        <DropdownItem
                                            href="#pablo"
                                            onClick={e => e.preventDefault()}
                                        >
                                            Another action
                                        </DropdownItem>
                                        <DropdownItem
                                            href="#pablo"
                                            onClick={e => e.preventDefault()}
                                        >
                                            Something else
                                        </DropdownItem>
                                    </DropdownMenu>
                                </UncontrolledDropdown>
                            </CardHeader>
                            <CardBody>
                                <div className="table-full-width table-responsive">
                                    <Table>
                                        <thead className="text-primary">
                                            <tr>
                                                <th>Company</th>
                                                <th>Shares</th>
                                                <th>Price per share</th>
                                                <th>Total value</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                allPositions.map(p =>
                                                    <tr key={`subscription-${p.stock.symbol}`}>
                                                        <td>
                                                            <p className="title">{p.stock.name}</p>
                                                            <p className="text-muted capatalize">
                                                                {p.stock.symbol}
                                                            </p>
                                                        </td>
                                                        <td>
                                                            <p className="title">{p.shares}</p>
                                                        </td>
                                                        <td>
                                                            <p className="title">{Intl.NumberFormat('nl-NL', {style: 'currency', currency: 'EUR', maximumFractionDigits: 2}).format(p.stock.market_value)}</p>
                                                        </td>
                                                        <td>
                                                            <p className="title">{Intl.NumberFormat('nl-NL', {style: 'currency', currency: 'EUR', maximumFractionDigits: 2}).format(p.stock.market_value * p.shares)}</p>
                                                        </td>
                                                        <td className="td-actions text-right">
                                                            <Link to={`/admin/subscription/${p.stock.symbol}`}>
                                                                <Button
                                                                    color="link"
                                                                    id={`tooltip-${p.stock.symbol}`}
                                                                    title=""
                                                                    type="button"
                                                                >
                                                                    <i className="tim-icons icon-bullet-list-67" />
                                                                </Button>
                                                            </Link>
                                                            <UncontrolledTooltip
                                                                delay={0}
                                                                target={`tooltip-${p.stock.symbol}`}
                                                                placement="right"
                                                            >
                                                                View transactions
                                                            </UncontrolledTooltip>
                                                        </td>
                                                    </tr>
                                                )
                                            }
                                        </tbody>
                                    </Table>
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                    <Col lg="6" md="12">
                        <Card>
                            <CardHeader>
                                <CardTitle tag="h4">Positions by sector</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <Table className="tablesorter" responsive>
                                    <thead className="text-primary">
                                        <tr>
                                            <th>Sector</th>
                                            <th>Positions</th>
                                            <th className="text-center">Share</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {
                                            Object.entries(positionsBySector).map(([sector, positions]) =>
                                                <tr key={sector}>
                                                    <td>{sectors[sector]?.name ?? sector + "Unknown sector"}</td>
                                                    <td>{positions?.length ?? 0 } </td>
                                                    <td>{((_.sum(positions?.map(p => p.shares * p.stock.market_value)) ?? 0) / (totalPositionValue ?? 0) * 100).toFixed(2)} %</td>
                                                </tr>
                                            )
                                        }
                                    </tbody>
                                </Table>
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </div>
        </>
    );
}

export default InvestmentsContainer;
