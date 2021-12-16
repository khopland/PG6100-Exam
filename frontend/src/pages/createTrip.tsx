import { Container, Form } from "react-bootstrap";
import React, { useContext, useEffect, useState } from "react";
import IBoat from "../inteface/IBoat";
import IPort from "../inteface/IPort";
import { createTrip } from "../service/tripService";
import { Status } from "../inteface/status";
import { useNavigate } from "react-router-dom";
import Button from "react-bootstrap/Button";
import { boatContext } from "../context/boatContext";
import { portContext } from "../context/portContext";
import { userContext } from "../context/userContext";

export const CreateTrip = () => {
  const { user } = useContext(userContext);
  const { boats, refresh: bRefresh } = useContext(boatContext);
  const { ports, refresh: pRefresh } = useContext(portContext);
  const [boat, setBoat] = useState<IBoat | null>(null);
  const [departure, setDeparture] = useState<IPort | null>(null);
  const [destination, setDestination] = useState<IPort | null>(null);
  const [passengers, setPassengers] = useState<number>(0);
  const [status, setStatus] = useState<Status>(Status.BOOKED);
  const navigate = useNavigate();
  useEffect(() => {
    bRefresh();
    pRefresh();
  }, []);

  function validateForm() {
    return (
      boat != null &&
      destination != null &&
      destination != null &&
      passengers > 0
    );
  }

  const handleSubmit = async (event: any) => {
    event.preventDefault();
    if (
      await createTrip(
        user!!.name!!,
        boat!!,
        departure!!,
        destination!!,
        passengers,
        status
      )
    ) {
      console.log("signed up");
      navigate("/");
    }
  };

  return (
    <Container style={{ paddingTop: "3rem" }}>
      <Form onSubmit={handleSubmit}>
        <Form.Group style={{ margin: "0 auto", maxWidth: "320px" }}>
          <Form.Select
            onChange={(x) =>
              setBoat(
                boats.filter((a) => a.id === Number(x.target.value)).shift()!!
              )
            }
          >
            <option>Select a Boat</option>
            {boats.map((x, i) => (
              <option key={i} value={x.id!!}>
                {x.id}
                {x.name}
              </option>
            ))}
          </Form.Select>
        </Form.Group>
        <Form.Group style={{ margin: "0 auto", maxWidth: "320px" }}>
          <Form.Select
            onChange={(x) =>
              setDeparture(
                ports.filter((a) => a.id === Number(x.target.value)).shift()!!
              )
            }
          >
            <option>Select departure port</option>
            {ports.map((x, i) => (
              <option key={i} value={x.id!!}>
                {x.id}
                {x.name}
              </option>
            ))}
          </Form.Select>
        </Form.Group>
        <Form.Group style={{ margin: "0 auto", maxWidth: "320px" }}>
          <Form.Select
            onChange={(x) =>
              setDestination(
                ports.filter((a) => a.id === Number(x.target.value)).shift()!!
              )
            }
          >
            <option>Select Destination port</option>
            {ports.map((x, i) => (
              <option key={i} value={x.id!!}>
                {x.id}
                {x.name}
              </option>
            ))}
          </Form.Select>
        </Form.Group>
        <Form.Group style={{ margin: "0 auto", maxWidth: "320px" }}>
          <Form.Label>number of Passengers</Form.Label>
          <Form.Control
            type="number"
            value={passengers}
            onChange={(e) => setPassengers(Number(e.target.value))}
          />
        </Form.Group>

        <Form.Group style={{ margin: "0 auto", maxWidth: "320px" }}>
          <Form.Select
            onChange={(x) => setStatus(Number(x.target.value) as Status)}
          >
            <option defaultChecked={true} value={Status.BOOKED}>
              {Status[Status.BOOKED]}
            </option>
            <option value={Status.ONGOING}>{Status[Status.ONGOING]}</option>
            <option value={Status.COMPLETE}>{Status[Status.COMPLETE]}</option>
            <option value={Status.CANCELLED}>{Status[Status.CANCELLED]}</option>
          </Form.Select>
        </Form.Group>

        <div
          style={{
            justifyContent: "center",
            display: "flex",
            alignItems: "center",
            paddingTop: "1rem",
          }}
        >
          <Button size="lg" type="submit" disabled={!validateForm()}>
            Submit
          </Button>
        </div>
      </Form>
    </Container>
  );
};
