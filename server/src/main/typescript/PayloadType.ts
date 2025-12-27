export enum PayloadType {
  BINARY = 0,
  JSON = 1,
}

export function getPayloadType(value: number): PayloadType {
  if (value === PayloadType.BINARY) {
    return PayloadType.BINARY;
  } else if (value === PayloadType.JSON) {
    return PayloadType.JSON;
  } else throw new Error("Unknown payload type value: " + value);
}
