package ru.zastolki.habit_tracker.controllers;

import org.springframework.web.bind.annotation.*;
import ru.zastolki.habit_tracker.entitys.HabitLog;
import ru.zastolki.habit_tracker.services.HabitLogService;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class HabitLogController {

    private final HabitLogService habitLogService;

    public HabitLogController(HabitLogService habitLogService) {
        this.habitLogService = habitLogService;
    }

    @PostMapping("/{habitId}")
    public HabitLog markDone(@PathVariable Long habitId) {
        return habitLogService.markDone(habitId);
    }

    @GetMapping("/{habitId}")
    public List<HabitLog> getLogs(@PathVariable Long habitId) {
        return habitLogService.getLogs(habitId);
    }
}