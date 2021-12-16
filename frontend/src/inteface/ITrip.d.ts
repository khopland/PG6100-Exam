import { Status } from "./status";

export default interface ITrip {
  id: number | null;
  userId: string;
  departure: number;
  destination: number;
  boat: number;
  passengers: number;
  status: Status;
}
