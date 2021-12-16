import React from "react";
import ReactDOM from "react-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import { Routing } from "./routing";
import { UserProvider } from "./context/userContext";
import { TripProvider } from "./context/tripContext";
import { BoatProvider } from "./context/boatContext";
import { PortProvider } from "./context/portContext";

ReactDOM.render(
  <React.StrictMode>
    <UserProvider>
      <TripProvider>
        <BoatProvider>
          <PortProvider>
            <Routing />
          </PortProvider>
        </BoatProvider>
      </TripProvider>
    </UserProvider>
  </React.StrictMode>,
  document.getElementById("root")
);
