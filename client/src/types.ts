import type { WhiteboardClient } from "./client";
import type { AppEvent } from "./messaging/types";

export type EventListener<T> = (
  event: AppEvent<T>,
  client: WhiteboardClient,
) => void;

export type AppSession = {
  id: string;
  users: User[];
};

export type User = {
  id: string;
  username: string;
};
