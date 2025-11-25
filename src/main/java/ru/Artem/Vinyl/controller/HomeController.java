package ru.Artem.Vinyl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для главной страницы.
 */
@Controller
public class HomeController {

    /**
     * Главная страница приложения.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}