package dev.oscarrojas.whiteboard.ws;

public class AppMessage {

  private String channel;
  private String action;
  private byte[] payload;

  public AppMessage() {}

  public AppMessage(String channel, String action, byte[] payload) {
    this.channel = channel;
    this.action = action;
    this.payload = payload;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String event) {
    this.action = event;
  }

  public byte[] getPayload() {
    return payload;
  }

  public void setPayload(byte[] payload) {
    this.payload = payload;
  }
}
