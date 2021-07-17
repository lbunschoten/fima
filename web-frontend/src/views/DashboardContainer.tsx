import React, {useEffect} from "react";
import classNames from "classnames";

import {
    Button,
    ButtonGroup,
    Card,
    CardBody,
    CardHeader,
    CardTitle,
    Col,
    DropdownItem,
    DropdownMenu,
    DropdownToggle,
    Row,
    Table,
    UncontrolledDropdown,
    UncontrolledTooltip
} from "reactstrap";
import {DashboardChart, fetchSubscriptions, fetchTransactions, fetchTransactionStatistics, setSelectedChart} from "./DashboardSlice";
import {useAppDispatch, useAppSelector} from "../index";
import {Link} from "react-router-dom";
import ChartComponent from "react-chartjs-2";

const DashboardContainer = () => {

    const dispatch = useAppDispatch()
    const selectedChart = useAppSelector((state) => state.dashboard.selectedChart)
    const transactions = useAppSelector((state) => state.dashboard.transactions)
    const subscriptions = useAppSelector((state) => state.dashboard.subscriptions)
    const chart: DashboardChart | null = useAppSelector((state) => {
        if (state.dashboard.selectedChart === "sum") return state.dashboard.sumChart
        else if (state.dashboard.selectedChart === "balance") return state.dashboard.balanceChart
        else return state.dashboard.countChart
    })

    useEffect(() => {
        dispatch(fetchTransactions())
        dispatch(fetchSubscriptions())
        dispatch(fetchTransactionStatistics())
    }, [dispatch])

    return (
        <>
            <div className="content">
                <Row>
                    <Col xs="12">
                        <Card className="card-chart">
                            <CardHeader>
                                <Row>
                                    <Col className="text-left" sm="6">
                                        <h5 className="card-category">Transactions</h5>
                                        <CardTitle tag="h2">Account status</CardTitle>
                                    </Col>
                                    <Col sm="6">
                                        <ButtonGroup
                                            className="btn-group-toggle float-right"
                                            data-toggle="buttons"
                                        >
                                            <Button
                                                tag="label"
                                                className={classNames("btn-simple", {
                                                    active: selectedChart === "count"
                                                })}
                                                color="info"
                                                id="0"
                                                size="sm"
                                                onClick={() => dispatch(setSelectedChart("count"))}
                                            >
                                                <input
                                                    defaultChecked
                                                    className="d-none"
                                                    name="options"
                                                    type="radio"
                                                />
                                                <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">Transactions</span>
                                                <span className="d-block d-sm-none">
                                                    <i className="tim-icons icon-single-02" />
                                                </span>
                                            </Button>
                                            <Button
                                                color="info"
                                                id="1"
                                                size="sm"
                                                tag="label"
                                                className={classNames("btn-simple", {
                                                    active: selectedChart === "sum"
                                                })}
                                                onClick={() => dispatch(setSelectedChart("sum"))}
                                            >
                                                <input
                                                    className="d-none"
                                                    name="options"
                                                    type="radio"
                                                />
                                                <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">Sum</span>
                                                <span className="d-block d-sm-none">
                                                    <i className="tim-icons icon-gift-2" />
                                                </span>
                                            </Button>
                                            <Button
                                                color="info"
                                                id="2"
                                                size="sm"
                                                tag="label"
                                                className={classNames("btn-simple", {
                                                    active: selectedChart === "balance"
                                                })}
                                                onClick={() => dispatch(setSelectedChart("balance"))}
                                            >
                                                <input
                                                    className="d-none"
                                                    name="options"
                                                    type="radio"
                                                />
                                                <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">Balance</span>
                                                <span className="d-block d-sm-none">
                                                    <i className="tim-icons icon-tap-02" />
                                                </span>
                                            </Button>
                                        </ButtonGroup>
                                    </Col>
                                </Row>
                            </CardHeader>
                            <CardBody>
                                <div className="chart-area">
                                    {chart != null && chart.options && chart.results.length > 0 && <ChartComponent
                                      data={(canvas: HTMLCanvasElement) => {
                                          let ctx = canvas.getContext("2d")!;

                                          let gradientStroke = ctx.createLinearGradient(0, 230, 0, 50);

                                          gradientStroke.addColorStop(1, "rgba(29,140,248,0.2)");
                                          gradientStroke.addColorStop(0.4, "rgba(29,140,248,0.0)");
                                          gradientStroke.addColorStop(0, "rgba(29,140,248,0)");

                                          return {
                                              labels: [
                                                  "JAN",
                                                  "FEB",
                                                  "MAR",
                                                  "APR",
                                                  "MAY",
                                                  "JUN",
                                                  "JUL",
                                                  "AUG",
                                                  "SEP",
                                                  "OCT",
                                                  "NOV",
                                                  "DEC"
                                              ],
                                              datasets: [
                                                  {
                                                      label: chart.label,
                                                      fill: true,
                                                      backgroundColor: gradientStroke,
                                                      borderColor: "#1f8ef1",
                                                      borderWidth: 2,
                                                      borderDash: [],
                                                      borderDashOffset: 0.0,
                                                      pointBackgroundColor: "#1f8ef1",
                                                      pointBorderColor: "rgba(255,255,255,0)",
                                                      pointHoverBackgroundColor: "#1f8ef1",
                                                      pointBorderWidth: 20,
                                                      pointHoverRadius: 4,
                                                      pointHoverBorderWidth: 15,
                                                      pointRadius: 4,
                                                      data: [...chart.results]
                                                  }
                                              ]
                                          }
                                      }}
                                      options={{...chart.options}}
                                      type='line' />}
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
                <Row>
                    <Col lg="6" md="12">
                        <Card className="card-tasks">
                            <CardHeader>
                                <h6 className="title d-inline">Subscriptions({subscriptions.length})</h6>
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
                                        <tbody>
                                            {
                                                subscriptions.map(s =>
                                                    <tr key={`subscription-${s.id}`}>
                                                        <td>
                                                            <p className="title">{s.name}</p>
                                                            <p className="text-muted capatalize">
                                                                {s.recurrence}
                                                            </p>
                                                        </td>
                                                        <td>
                                                            <p className="title">{s.name}</p>
                                                        </td>
                                                        <td className="td-actions text-right">
                                                            <Link to={`/admin/subscription/${s.id}`}>
                                                                <Button
                                                                    color="link"
                                                                    id={`tooltip-${s.id}`}
                                                                    title=""
                                                                    type="button"
                                                                >
                                                                    <i className="tim-icons icon-bullet-list-67" />
                                                                </Button>
                                                            </Link>
                                                            <UncontrolledTooltip
                                                                delay={0}
                                                                target={`tooltip-${s.id}`}
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
                                <CardTitle tag="h4">Recent transactions</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <Table className="tablesorter" responsive>
                                    <thead className="text-primary">
                                        <tr>
                                            <th>Date</th>
                                            <th>Description</th>
                                            <th className="text-center">Amount</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {
                                            transactions.map(t =>
                                                <tr key={t.id}>
                                                    <td>{t.date}</td>
                                                    <td>{t.name}</td>
                                                    <td className="text-center">&euro;{parseFloat((t.amount / 100).toString()).toFixed(2)}</td>
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

export default DashboardContainer;
