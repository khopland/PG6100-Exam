import axios from "axios";
import IPage from "../inteface/IPage";
import ITrip from "../inteface/ITrip";
import IBoat from "../inteface/IBoat";
import IPort from "../inteface/IPort";
import { Status } from "../inteface/status";

export const getATrip = async (id: number) => {
  try {
    const res = await axios.get("/api/trips/" + id, { withCredentials: true });
    if (res.status === 200) return res.data.data as ITrip;
  } catch (e) {}
  return null;
};

export const getTrips = async () => {
  try {
    const res = await axios.get("/api/trips", { withCredentials: true });
    if (res.status === 200) {
      const trips = res.data.data as IPage<ITrip>;
      let next = trips.next;
      let list = trips.list;
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
    if (res.status === 200) return res.data.data as IPage<ITrip>;
  } catch (e) {
    console.error(e);
  }
  return null;
};

export const createTrip = async (
  userId: string,
  boat: IBoat,
  departure: IPort,
  destination: IPort,
  passengers: number,
  status: Status
) => {
  try {
    const res = await axios.post(
      "/api/trips",
      {
        boat: boat.id,
        departure: departure.id,
        destination: destination.id,
        passengers: passengers,
        status: status,
        userId: userId,
      },
      { withCredentials: true }
    );
    return res.status === 201;
  } catch (e) {
    console.error(e);
    return false;
  }
};
