import React, { useContext, useEffect, useMemo, useState } from "react";
import { tripContext } from "../context/tripContext";
import { boatContext } from "../context/boatContext";
import { portContext } from "../context/portContext";
import Container from "react-bootstrap/esm/Container";
import {
  Col,
  Dropdown,
  DropdownButton,
  InputGroup,
  Row,
  Table,
} from "react-bootstrap";
import IPort from "../inteface/IPort";
import IBoat from "../inteface/IBoat";
import { Status } from "../inteface/status";

export const Trips = () => {
  const { trips, refresh: tRefresh } = useContext(tripContext);
  const { boats, refresh: bRefresh } = useContext(boatContext);
  const { ports, refresh: pRefresh } = useContext(portContext);
  const [port, setPort] = useState<IPort | null>(null);
  const [boat, setBoat] = useState<IBoat | null>(null);

  useEffect(() => {
    bRefresh();
    pRefresh();
    tRefresh();
  }, []);

  const mem = useMemo(
    () =>
      trips.filter(
        (x) =>
          x.boat == boat?.id ||
          x.departure == port?.id ||
          x.destination == port?.id
      ),
    [port, boat, trips]
  );
  return (
    <Container style={{ paddingTop: "3rem" }}>
      <Row>
        <Col>
          <InputGroup>
            <DropdownButton
              variant="outline-secondary"
              title={port ? port.name : "ports"}
            >
              {ports.map((p, i) => (
                <Dropdown.Item
                  key={i}
                  onClick={(_) => {
                    setPort(p);
                    setBoat(null);
                  }}
                >
                  {p.id + " " + p.name}
                </Dropdown.Item>
              ))}
            </DropdownButton>
          </InputGroup>
        </Col>
        <Col>
          <InputGroup>
            <DropdownButton
              variant="outline-secondary"
              title={boat ? boat.name : "boats"}
            >
              {boats.map((b, i) => (
                <Dropdown.Item
                  key={i}
                  onClick={(_) => {
                    setBoat(b);
                    setPort(null);
                  }}
                >
                  {b.id + " " + b.name}
                </Dropdown.Item>
              ))}
            </DropdownButton>
          </InputGroup>
        </Col>
      </Row>
      <Table bordered hover>
        <thead>
          <tr>
            <th>id</th>
            <th>boatId</th>
            <th>departure portId</th>
            <th>destination portId</th>
            <th>passengers</th>
            <th>status</th>
          </tr>
        </thead>
        <tbody>
          {boat != null || port != null
            ? mem.map((x, i) => (
                <tr key={i}>
                  <td>{x.id}</td>
                  <td>{x.boat}</td>
                  <td>{x.departure}</td>
                  <td>{x.destination}</td>
                  <td>{x.passengers}</td>
                  <td>{x.status as Status}</td>
                </tr>
              ))
            : trips.map((x, i) => (
                <tr key={i}>
                  <td>{x.id}</td>
                  <td>{x.boat}</td>
                  <td>{x.departure}</td>
                  <td>{x.destination}</td>
                  <td>{x.passengers}</td>
                  <td>{x.status as Status}</td>
                </tr>
              ))}
        </tbody>
      </Table>
    </Container>
  );
};
