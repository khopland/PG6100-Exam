import React from "react";
import ReactDOM from "react-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import { Routing } from "./routing";
import { UserProvider } from "./context/userContext";

ReactDOM.render(
  <React.StrictMode>
    <UserProvider>
      <Routing />
    </UserProvider>
  </React.StrictMode>,
  document.getElementById("root")
);
