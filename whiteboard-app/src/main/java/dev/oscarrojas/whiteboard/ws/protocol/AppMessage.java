package dev.oscarrojas.whiteboard.ws.protocol;

public class AppMessage {

  private String event;
  private byte[] payload;

  public AppMessage() {
  }

  public AppMessage(String event, byte[] payload) {
    this.event = event;
    this.payload = payload;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public byte[] getPayload() {
    return payload;
  }

  public void setPayload(byte[] payload) {
    this.payload = payload;
  }

}
