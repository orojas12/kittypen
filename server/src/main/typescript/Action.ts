export enum Action {
  CREATE_LOBBY = "CREATE_LOBBY",
}

export function getAction(value: string): Action {
  if (value === Action.CREATE_LOBBY) {
    return Action.CREATE_LOBBY;
  } else {
    throw new Error("Unknown action value: " + value);
  }
}
