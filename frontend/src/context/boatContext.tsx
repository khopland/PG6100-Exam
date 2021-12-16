import { createContext, FC, useEffect, useState } from "react";
import IBoat from "../inteface/IBoat";
import { getBoats } from "../service/boatService";

type IBoatContext = {
  boats: IBoat[];
  refresh: () => Promise<void>;
};

export const boatContext = createContext<IBoatContext>({
  boats: [],
  refresh: async () => {},
});

export const BoatProvider: FC = ({ children }) => {
  const [boats, setContext] = useState<IBoat[]>([]);
  useEffect(() => {
    getContext();
  }, []);
  const getContext = async () => {
    const boats = await getBoats();
    if (boats != null) setContext(boats);
  };

  return (
    <boatContext.Provider value={{ boats, refresh: getContext }}>
      {children}
    </boatContext.Provider>
  );
};
