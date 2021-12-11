import Container from "react-bootstrap/esm/Container";
import Nav from "react-bootstrap/esm/Nav";
import Navbar from "react-bootstrap/esm/Navbar";
import { Link } from "react-router-dom";
import { userContext } from "../context/userContext";
import { useContext } from "react";

export const NavBar = () => {
  const { user, isAdmin } = useContext(userContext);
  return (
    <Navbar bg="dark" variant="dark">
      <Container>
        <Navbar.Brand as={Link} to="/">
          game
        </Navbar.Brand>
        <Nav>
          <Nav.Link as={Link} to="/">
            home
          </Nav.Link>
          {user ? (
            <>
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

          {isAdmin() && (
            <Nav.Link as={Link} to="admin">
              Admin
            </Nav.Link>
          )}
        </Nav>
      </Container>
    </Navbar>
  );
};
