export type AppMessage = {
  timestamp: number;
  channel: string;
  action: string;
  payload: ArrayBuffer;
};
