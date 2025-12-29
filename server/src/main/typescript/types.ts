import { Action } from "@/Action";

export type ProtocolMessage = {
  timestamp: Date;
  action: Action;
  payload: Uint8Array | {};
};
