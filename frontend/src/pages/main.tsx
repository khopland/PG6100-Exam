import axios from "axios";
import React, { useEffect, useState } from "react";

export const Main = () => {
  const [user, setUser] = useState("")
  useEffect(() => {
    axios.get("api/auth/user",{withCredentials: true}).then(x=> {
      if(x.status === 200)
        setUser(x.data)
    })
 
  }, [])
  return <div>main
    {user && JSON.stringify(user)}
  </div>;
};
