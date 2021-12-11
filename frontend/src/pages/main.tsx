import React, { useContext } from "react";
import { userContext } from "../context/userContext";

export const Main = () => {
  const { user } = useContext(userContext);
  return (
    <div>
      {user && (
        <>
          <h1>{user.name}</h1>
          <ul>
            {user.roles.map((x, i) => (
              <li key={i}>{x}</li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
};
