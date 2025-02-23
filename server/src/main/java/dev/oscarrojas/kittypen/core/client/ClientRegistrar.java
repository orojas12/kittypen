package dev.oscarrojas.kittypen.core.client;

public interface ClientRegistrar {

    String getRegistrarName();

    void registerClient(ClientRegistration clientRegistration);

}
