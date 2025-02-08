package dev.oscarrojas.whiteboard.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/session")
public class UserSessionController {

    private final SecurityContextRepository securityContextRepository;

    UserSessionController(
        SecurityContextRepository securityContextRepository
    ) {
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping
    public void initUserSession(
        @CurrentSecurityContext SecurityContext context,
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody AnonymousUserDetails user
    ) {
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        HttpSession session = request.getSession(false);
        assert session != null;
        session.setAttribute("username", user.getUsername());
    }
}
