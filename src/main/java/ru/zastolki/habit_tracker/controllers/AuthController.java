package ru.zastolki.habit_tracker.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.zastolki.habit_tracker.dto.RegisterDto;
import ru.zastolki.habit_tracker.services.UserService;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterDto registerDto) {
        log.info("Получен HTTP-запрос на регистрацию пользователя: метод=POST путь=/register имяПользователя={}",
                registerDto.getUsername());

        userService.register(registerDto);
        return "success";
    }
}
