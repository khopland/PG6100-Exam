export default interface IBoat {
  id: number | null;
  name: string;
  builder: string;
  numberOfCrew: number;
  maxPassengers: number;
  minPassengers: number;
}
