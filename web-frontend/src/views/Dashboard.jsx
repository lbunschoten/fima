import React from "react";
import classNames from "classnames";
import ChartComponent from "react-chartjs-2";

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

import {transactionsChart} from "../variables/charts.jsx";
import {Link} from "react-router-dom";

class Dashboard extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedChart: "count",
      count: () => ({}),
      sum: () => ({}),
      balance: () => ({}),
      transactions: [],
      subscriptions: []
    };
  }

  transactionsChartData(label, results) {
    return canvas => {
      let ctx = canvas.getContext("2d");

      let gradientStroke = ctx.createLinearGradient(0, 230, 0, 50);

      gradientStroke.addColorStop(1, "rgba(29,140,248,0.2)");
      gradientStroke.addColorStop(0.4, "rgba(29,140,248,0.0)");
      gradientStroke.addColorStop(0, "rgba(29,140,248,0)"); //blue colors

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
            label: label,
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
            data: results
          }
        ]
      }
    }
  }

  componentDidMount() {
    fetch("http://api.fima.test/transaction/statistics") // FIXME: Don't use hardcoded domain name
      .then(res => res.json())
      .then(results => {
        this.setState({
            count: {
              data: this.transactionsChartData("# of transactions", results.map(stats => stats.transactions)),
              options: transactionsChart.defaultChartOptions
            },
            sum: {
              data: this.transactionsChartData("Sum of all transactions", results.map(stats => stats.sum)),
              options: transactionsChart.currencyChartOptions
            },
            balance: {
              data: this.transactionsChartData("Balance", results.map(stats => stats.balance)),
              options: transactionsChart.currencyChartOptions
            }
          }
        );
      });

    fetch("http://api.fima.test/transaction/recent?offset=0&limit=10") // FIXME: Don't use hardcoded domain name
      .then(res => res.json())
      .then(results => {
        this.setState({
          transactions: results
        });
      });

    fetch("http://api.fima.test/subscriptions")
      .then(res => res.json())
      .then(results => {
        this.setState({
          subscriptions: results
        });
      });
  }

  setSelectedChart = name => {
    this.setState({
      selectedChart: name
    });
  };

  render() {
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
                            active: this.state.selectedChart === "count"
                          })}
                          color="info"
                          id="0"
                          size="sm"
                          onClick={() => this.setSelectedChart("count")}
                        >
                          <input
                            defaultChecked
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Transactions
                          </span>
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
                            active: this.state.selectedChart === "sum"
                          })}
                          onClick={() => this.setSelectedChart("sum")}
                        >
                          <input
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Sum
                          </span>
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
                            active: this.state.selectedChart === "balance"
                          })}
                          onClick={() => this.setSelectedChart("balance")}
                        >
                          <input
                            className="d-none"
                            name="options"
                            type="radio"
                          />
                          <span className="d-none d-sm-block d-md-block d-lg-block d-xl-block">
                            Balance
                          </span>
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
                    <ChartComponent
                      data={this.state[this.state.selectedChart].data}
                      options={this.state[this.state.selectedChart].options}
                      type='line' />
                  </div>
                </CardBody>
              </Card>
            </Col>
          </Row>
          <Row>
            <Col lg="6" md="12">
              <Card className="card-tasks">
                <CardHeader>
                  <h6 className="title d-inline">Subscriptions({this.state.subscriptions.length})</h6>
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
                          this.state.subscriptions.map(s =>
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
                        this.state.transactions.map(t =>
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
}

export default Dashboard;
