package dev.oscarrojas.drawandguess.core.user;

public class ClientRegistration {

    private String id;
    private String username;
    private String registrar;

    public ClientRegistration(String id, String username, String registrar) {
        this.id = id;
        this.username = username;
        this.registrar = registrar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegistrar() {
        return registrar;
    }

    public void setRegistrar(String registrar) {
        this.registrar = registrar;
    }

}
