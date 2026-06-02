package ru.zastolki.habit_tracker.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.zastolki.habit_tracker.entitys.HabitLog;
import ru.zastolki.habit_tracker.services.HabitLogService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class HabitLogController {

    private static final Logger log = LoggerFactory.getLogger(HabitLogController.class);

    private final HabitLogService habitLogService;

    public HabitLogController(HabitLogService habitLogService) {
        this.habitLogService = habitLogService;
    }

    @PostMapping("/{habitId}")
    public HabitLog markDone(@PathVariable Long habitId, Principal principal) {
        var username = principal.getName();
        log.info("Получен HTTP-запрос на отметку выполнения привычки: метод=POST путь=/logs/{} имяПользователя={}",
                habitId,
                username);
        return habitLogService.markDone(habitId, username);
    }

    @GetMapping("/{habitId}")
    public List<HabitLog> getLogs(@PathVariable Long habitId, Principal principal) {
        var username = principal.getName();
        log.info("Получен HTTP-запрос на получение журнала привычки: метод=GET путь=/logs/{} имяПользователя={}",
                habitId,
                username);
        return habitLogService.getLogs(habitId, username);
    }
}
