import { createContext, FC, useEffect, useState } from "react";
import { getUser } from "../service/userService";

export type IUser = {
  name: string;
  roles: string[];
};
type IUserContext = {
  user: IUser | null;
  isAdmin: () => boolean;
  refresh: () => Promise<void>;
};

export const userContext = createContext<IUserContext>({
  user: null,
  isAdmin: () => {
    throw new Error("Context not initialized");
  },
  refresh: async () => {},
});

export const UserProvider: FC = ({ children }) => {
  const [user, setContext] = useState<IUser | null>(null);
  useEffect(() => {
    getContext();
  }, []);
  const getContext = async () => {
    const user = await getUser();
    setContext(user);
  };
  const isAdmin = () => user != null && user?.roles.includes("ROLE_ADMIN");
  return (
    <userContext.Provider value={{ user, isAdmin, refresh: getContext }}>
      {children}
    </userContext.Provider>
  );
};
