import axios from "axios";
import IPage from "../inteface/IPage";
import IPort from "../inteface/IPort";

export const getAPort = async (id: number) => {
  try {
    const res = await axios.get("/api/port/" + id, { withCredentials: true });
    if (res.status === 200) return res.data.data as IPort;
  } catch (e) {}
  return null;
};

export const getPorts = async () => {
  try {
    const res = await axios.get("/api/port", { withCredentials: true });
    if (res.status === 200) {
      const port = res.data.data as IPage<IPort>;
      let next = port.next;
      let list = port.list;
      while (next != null) {
        const res = await getNextPage(next);
        if (res == null) break;
        next = res.next;
        list = list.concat(res.list);
      }
      return list;
    }
  } catch (e) {}
  return null;
};

const getNextPage = async (next: string) => {
  try {
    const res = await axios.get(next, { withCredentials: true });
    if (res.status === 200) return res.data.data as IPage<IPort>;
  } catch (e) {
    console.error(e);
  }
  return null;
};
