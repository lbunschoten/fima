import React, {useEffect} from "react";
import { RouteComponentProps } from "react-router-dom";

import {Card, CardBody, CardHeader, CardTitle, Col, Row, Table} from "reactstrap";
import {useAppDispatch, useAppSelector} from "../index";
import {fetchTransactions} from "./SubscriptionSlice";

interface UrlParams {
    id: string
}

const SubscriptionContainer = (props: RouteComponentProps<UrlParams>) => {

    const dispatch = useAppDispatch()
    const subscription = useAppSelector((state) => state.subscription.subscription)
    const transactions = useAppSelector((state) => state.subscription.transactions)

    useEffect(() => {
        dispatch(fetchTransactions(props.match.params.id))
    }, [dispatch, props.match.params.id])

    return (
        <>
            <div className="content">
                <Row>
                    <Col md="12">
                        <Card>
                            <CardHeader>
                                <CardTitle tag="h4">{subscription?.name ?? ""} transactions</CardTitle>
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

export default SubscriptionContainer;
