package ru.Artem.Vinyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.Artem.Vinyl.entity.User;
import ru.Artem.Vinyl.service.UserService;

/**
 * Контроллер для регистрации новых пользователей.
 */
@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Показывает форму регистрации.
     */
    @GetMapping("/registration")
    public String showRegistrationForm() {
        return "registration";
    }

    /**
     * Обрабатывает отправку формы регистрации.
     *
     */
    @PostMapping("/registration")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        try {
            // Создаём нового пользователя
            User user = new User(username, password);

            // Добавляем пользователя в БД (пароль будет автоматически зашифрован)
            userService.addUser(user);

            // Перенаправляем на страницу логина
            return "redirect:/login?registered";

        } catch (IllegalArgumentException ex) {
            // Если пользователь с таким именем уже существует
            model.addAttribute("message", "Пользователь с таким именем уже существует");
            return "registration";
        } catch (Exception ex) {
            // Любая другая ошибка
            model.addAttribute("message", "Ошибка регистрации: " + ex.getMessage());
            return "registration";
        }
    }
}