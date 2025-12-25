package dev.oscarrojas.drawandguess.web;

import dev.oscarrojas.drawandguess.core.exceptions.LobbyFullException;
import dev.oscarrojas.drawandguess.core.lobby.LobbyManager;
import dev.oscarrojas.drawandguess.core.lobby.LobbyUserRegistration;
import dev.oscarrojas.drawandguess.dto.CreateLobby;
import dev.oscarrojas.drawandguess.dto.JoinLobby;
import dev.oscarrojas.drawandguess.dto.LobbyData;
import dev.oscarrojas.drawandguess.dto.UserData;
import dev.oscarrojas.drawandguess.exception.NotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class IndexController {

    private final LobbyManager lobbyManager;

    public IndexController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("createLobby", new CreateLobby(null));
        return "index";
    }

    @PostMapping("/lobby/create")
    public String createLobby(HttpSession session, CreateLobby dto) {
        String lobbyId = lobbyManager.createLobby();

        try {
            lobbyManager.addUserToLobby(new UserData(dto.username()), lobbyId);
        } catch (NotFoundException | LobbyFullException e) {
            throw new RuntimeException(e);
        }

        session.setAttribute("lobbyId", lobbyId);

        return "redirect:/lobby";
    }

    @GetMapping("/lobby/join")
    public String joinLobbyForm(Model model) {
        model.addAttribute("joinLobby", new JoinLobby(null, null));

        return "join-lobby";
    }

    @PostMapping("/lobby/join")
    public String joinLobby(HttpSession session, JoinLobby dto) {
        LobbyUserRegistration registration;
        try {
            registration = lobbyManager.addUserToLobby(
                    new UserData(dto.username()),
                    dto.lobbyId()
            );
        } catch (NotFoundException | LobbyFullException e) {
            throw new RuntimeException(e);
        }

        session.setAttribute("lobbyId", registration.lobbyId());

        return "redirect:/lobby";
    }

    @GetMapping("/lobby")
    public String lobby(Model model, HttpSession session) {
        String lobbyId = (String) session.getAttribute("lobbyId");
        Optional<LobbyData> lobbyOpt = lobbyManager.getLobby(lobbyId);
        LobbyData lobby = lobbyOpt.orElseThrow();
        model.addAttribute("lobby", lobby);
        return "lobby";
    }


}
