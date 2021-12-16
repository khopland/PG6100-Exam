import { createContext, FC, useEffect, useState } from "react";
import IPort from "../inteface/IPort";
import { getPorts } from "../service/portService";

type IPortContext = {
  ports: IPort[];
  refresh: () => Promise<void>;
};

export const portContext = createContext<IPortContext>({
  ports: [],
  refresh: async () => {},
});

export const PortProvider: FC = ({ children }) => {
  const [ports, setContext] = useState<IPort[]>([]);
  useEffect(() => {
    getContext();
  }, []);
  const getContext = async () => {
    const ports = await getPorts();
    if (ports != null) setContext(ports);
  };

  return (
    <portContext.Provider value={{ ports, refresh: getContext }}>
      {children}
    </portContext.Provider>
  );
};
