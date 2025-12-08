package dev.oscarrojas.drawandguess.core.user;

public interface ClientRegistrar {

    String getRegistrarName();

    void registerClient(ClientRegistration clientRegistration);

}
