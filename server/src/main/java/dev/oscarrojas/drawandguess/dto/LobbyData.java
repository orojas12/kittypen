package dev.oscarrojas.drawandguess.dto;

import java.util.List;

public record LobbyData(String lobbyId, List<String> users) {}
