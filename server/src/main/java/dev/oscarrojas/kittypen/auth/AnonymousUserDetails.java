package dev.oscarrojas.kittypen.auth;

public class AnonymousUserDetails {

    private String username;

    public AnonymousUserDetails(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
