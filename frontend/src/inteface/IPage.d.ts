export default interface IPage<T> {
  list: T[];
  next: string | null | undefined;
}
