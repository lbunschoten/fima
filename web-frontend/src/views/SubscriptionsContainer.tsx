import React, {useEffect} from "react";

import {Button, Card, CardBody, CardHeader, CardTitle, Col, Row, Table, UncontrolledTooltip} from "reactstrap";
import {Link} from "react-router-dom";
import {useAppDispatch, useAppSelector} from "../index";
import {fetchSubscriptions} from "./SubscriptionsSlice";


const SubscriptionsContainer = () => {

    const dispatch = useAppDispatch()
    const subscriptions = useAppSelector((state) => state.subscriptions.subscriptions)

    useEffect(() => {
        dispatch(fetchSubscriptions())
    }, [dispatch]);

    return (
        <>
            <div className="content">
                <Row>
                    <Col md="12">
                        <Card>
                            <CardHeader>
                                <CardTitle tag="h4">Subscriptions</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <Table className="tablesorter" responsive>
                                    <tbody>
                                        {
                                            subscriptions.map(s =>
                                                <tr key={`subscription-${s.id}`}>
                                                    <td>
                                                        <p className="title">{s.name}</p>
                                                        <p className="text-muted">
                                                            {s.name}
                                                        </p>
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
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </div>
        </>
    );
}

export default SubscriptionsContainer;
