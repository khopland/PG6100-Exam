import axios from "axios";
import IUser from "../inteface/IUser";

export const getUser = async () => {
  try {
    const res = await axios.get("/api/auth/user", { withCredentials: true });
    if (res.status === 200) return res.data as IUser;
  } catch (e) {}
  return null;
};
export const login = async (userId: string, password: string) => {
  try {
    const res = await axios.post(
      "/api/auth/login",
      {
        userId,
        password,
      },
      { withCredentials: true }
    );
    return res.status === 204;
  } catch (e) {
    console.error(e);
    return false;
  }
};
export const singUp = async (userId: string, password: string) => {
  try {
    const res = await axios.post(
      "/api/auth/signUp",
      {
        userId,
        password,
      },
      { withCredentials: true }
    );
    return res.status === 201;
  } catch (e) {
    console.error(e);
    return false;
  }
};
export const logOut = async () => {
  try {
    const res = await axios.get("/api/auth/logout", { withCredentials: true });
    return res.status === 204;
  } catch (e) {
    console.error(e);
    return false;
  }
};
