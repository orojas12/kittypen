import { EventListener } from "./types";

const onSessionUpdate: EventListener<string> = (event, client): void => {
  const data = JSON.parse(event.payload);
};
