import axios from "axios";
import React, { useState } from "react";
import Button from "react-bootstrap/esm/Button";
import Container from "react-bootstrap/esm/Container";
import Form from "react-bootstrap/esm/Form";
import { useNavigate } from "react-router-dom";

export const Signup = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [checkPassword, setCheckPassword] = useState("");
  const navigate = useNavigate();

  function validateForm() {
    return email.length > 0 && password.length > 0 && checkPassword === password;
  }

  async function handleSubmit(event: any) {
    event.preventDefault();
    const res = await axios.post("/api/auth/signUp",{"userId":email,"password":password})
    if (res.status ===201){
      console.log("signed up")
      navigate("/",{replace: true})
    }
  }
  return (
    <Container className="Login" style={{ padding: "60px 0" }}>
      <Form onSubmit={handleSubmit}>
        <Form.Group
          style={{ margin: "0 auto", maxWidth: "320px" }}
          controlId="email"
        >
          <Form.Label>Email</Form.Label>
          <Form.Control
            autoFocus
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </Form.Group>
        <Form.Group
          style={{ margin: "0 auto", maxWidth: "320px" }}
          controlId="password"
        >
          <Form.Label>Password</Form.Label>
          <Form.Control
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </Form.Group>
        <Form.Group
          style={{ margin: "0 auto", maxWidth: "320px" }}
          controlId="checkPassword"
        >
          <Form.Label>Confirm Password</Form.Label>
          <Form.Control
            type="password"
            value={checkPassword}
            onChange={(e) => setCheckPassword(e.target.value)}
          />
        </Form.Group>
        <div
          style={{
            justifyContent: "center",
            display: "flex",
            alignItems: "center",
            paddingTop:"1rem"
          }}
        >
          <Button size="lg" type="submit" disabled={!validateForm()}>
            Sign up
          </Button>
        </div>
      </Form>
    </Container>)
};
