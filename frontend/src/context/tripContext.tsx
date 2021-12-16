import { createContext, FC, useEffect, useState } from "react";
import ITrip from "../inteface/ITrip";
import { getTrips } from "../service/tripService";

type ITripContext = {
  trips: ITrip[];
  refresh: () => Promise<void>;
};

export const tripContext = createContext<ITripContext>({
  trips: [],
  refresh: async () => {},
});

export const TripProvider: FC = ({ children }) => {
  const [trips, setContext] = useState<ITrip[]>([]);
  useEffect(() => {
    getContext();
  }, []);
  const getContext = async () => {
    const trips = await getTrips();
    if (trips != null) setContext(trips);
  };
  return (
    <tripContext.Provider value={{ trips, refresh: getContext }}>
      {children}
    </tripContext.Provider>
  );
};
