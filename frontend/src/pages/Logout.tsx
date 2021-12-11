import { useContext, useEffect } from "react";
import { logOut } from "../service/userService";
import { userContext } from "../context/userContext";
import { useNavigate } from "react-router-dom";

export const Logout = () => {
  const { user, refresh } = useContext(userContext);
  const navigate = useNavigate();

  useEffect(() => {
    logout();
  }, []);
  const logout = async () => {
    console.log(user);
    await logOut();
    await refresh();
    navigate("/", { replace: true });
  };
  return <div>logout</div>;
};
