package dev.oscarrojas.drawandguess.core.user;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientRegistrarFactory {

    private final Map<String, ClientRegistrar> registrarMap;

    public ClientRegistrarFactory(List<ClientRegistrar> registrars) {
        registrarMap = new HashMap<>();
        for (ClientRegistrar registrar : registrars) {
            registrarMap.put(registrar.getRegistrarName(), registrar);
        }
    }

    @Nullable
    public ClientRegistrar getRegistrar(String registrar) {
        return registrarMap.get(registrar);
    }
}
