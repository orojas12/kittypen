export type AppEvent<T> = {
  timestamp: number;
  name: string;
  payload: T;
};
