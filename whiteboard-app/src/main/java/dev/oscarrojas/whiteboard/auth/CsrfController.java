package dev.oscarrojas.whiteboard.auth;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/csrf")
public class CsrfController {

    @GetMapping
    public String getCsrfToken(CsrfToken token) {
//        token.getToken();
        return "csrf token";
    }
}
