package ru.Artem.Vinyl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для страницы логина.
 */
@Controller
public class LoginController {

    /**
     * Показывает форму входа в систему.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}