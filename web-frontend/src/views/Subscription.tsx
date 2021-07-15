import React from "react";

import {Card, CardBody, CardHeader, CardTitle, Col, Row, Table} from "reactstrap";
import {RouteComponentProps} from "react-router";
import {SubscriptionData} from "../variables/SubscriptionData";
import {TransactionData} from "../variables/TransactionData";

interface UrlParams {
    id: string
}

interface SubscriptionProps extends RouteComponentProps<UrlParams> {
}

interface SubscriptionState {
    subscription: SubscriptionData | null,
    transactions: TransactionData[]
}

class Subscription extends React.Component<SubscriptionProps, SubscriptionState> {

    constructor(props: Readonly<SubscriptionProps>) {
        super(props);
        this.state = {
            subscription: null,
            transactions: []
        }
    }

    componentDidMount() {
        fetch(`http://api.fima.test/subscription/${this.props.match.params.id}`)
            .then(res => res.json())
            .then(result => {
                this.setState({
                    subscription: result.subscription,
                    transactions: result.transactions
                });
            });
    }

    render() {
        return (
            <>
                <div className="content">
                    <Row>
                        <Col md="12">
                            <Card>
                                <CardHeader>
                                    <CardTitle tag="h4">{this.state.subscription?.name ?? ""} transactions</CardTitle>
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

export default Subscription;
