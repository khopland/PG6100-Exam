import Container from "react-bootstrap/esm/Container";
import Nav from "react-bootstrap/esm/Nav";
import Navbar from "react-bootstrap/esm/Navbar";
import { Link } from "react-router-dom";
import { userContext } from "../context/userContext";
import { useContext } from "react";

export const NavBar = () => {
  const { user } = useContext(userContext);
  return (
    <Navbar bg="dark" variant="dark">
      <Container>
        <Navbar.Brand as={Link} to="/">
          boat navigation
        </Navbar.Brand>
        <Nav>
          <Nav.Link as={Link} to="/">
            home
          </Nav.Link>
          <Nav.Link as={Link} to="trips">
            trips
          </Nav.Link>
          {user ? (
            <>
              <Nav.Link as={Link} to="create">
                create Trip
              </Nav.Link>
              <Nav.Link as={Link} to="logout">
                logout
              </Nav.Link>
            </>
          ) : (
            <>
              <Nav.Link as={Link} to="signup">
                Signup
              </Nav.Link>
              <Nav.Link as={Link} to="login">
                Login
              </Nav.Link>
            </>
          )}
        </Nav>
      </Container>
    </Navbar>
  );
};
