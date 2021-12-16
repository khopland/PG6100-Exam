import axios from "axios";
import IPage from "../inteface/IPage";
import IBoat from "../inteface/IBoat";

export const getABoat = async (id: number) => {
  try {
    const res = await axios.get("/api/boat/" + id, { withCredentials: true });
    if (res.status === 200) return res.data.data as IBoat;
  } catch (e) {}
  return null;
};

export const getBoats = async () => {
  try {
    const res = await axios.get("/api/boat", { withCredentials: true });
    if (res.status === 200) {
      const boats = res.data.data as IPage<IBoat>;
      let next = boats.next;
      let list = boats.list;
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
    if (res.status === 200) return res.data.data as IPage<IBoat>;
  } catch (e) {
    console.error(e);
  }
  return null;
};
