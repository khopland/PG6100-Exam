import React, { useContext, useEffect } from "react";
import { userContext } from "../context/userContext";
import Container from "react-bootstrap/esm/Container";
import { tripContext } from "../context/tripContext";
import { Row, Table } from "react-bootstrap";
import { Status } from "../inteface/status";

export const Main = () => {
  const { user } = useContext(userContext);
  const { trips, refresh } = useContext(tripContext);
  useEffect(() => {
    refresh();
  }, []);

  return (
    <Container style={{ paddingTop: "3rem" }}>
      {user ? (
        <>
          <h1>Hello {user.name}</h1>
          <Row>
            <h2>your trips</h2>
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
              {trips
                .filter((x) => x.userId == user?.name)
                .map((x, i) => (
                  <tr key={i}>
                    <td>{x.id}</td>
                    <td>{x.boat}</td>
                    <td>{x.departure}</td>
                    <td>{x.destination}</td>
                    <td>{x.passengers}</td>
                    <td>{x.status as Status}</td>
                  </tr>
                ))}
            </Table>
          </Row>
        </>
      ) : (
        <h1>Need to sign in :)</h1>
      )}
    </Container>
  );
};
